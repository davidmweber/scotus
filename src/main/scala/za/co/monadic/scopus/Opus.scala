/*
 * Copyright David Weber 2014
 * Released under the Creative Commons License (http://creativecommons.org/licenses/by/4.0/legalcode)
 */
package za.co.monadic.scopus

import java.io.{FileOutputStream, File}
import java.util.UUID
import java.nio.channels.Channels


/**
 * General loader for dynamic libraries from the resources directory in the jar file.
 * It copies the required library from the jar to the target temporary directory and then
 * optionally loads the file using System.load(). It will clean the directory on JVM
 * shutdown using shutdown hooks. It is clever enough to find the correct
 * directory for the native version of the library (although this is untested). Note that
 * you need to build the JNI native stubs with some care so that dependent libraries
 * can be loaded. In Linux for example, Java must load libjni_opus.so (JNI native code).
 * The actual Opus library is loaded by the dynamic linker from a dependency in the stub.
 * Libjni_opus.so is built to look in the same directory for libopus, so we must copy
 * it to a temporary directory as well, but there is no need to ask the JVM to load it.
 * In Windows, you will need to load it as well.
 *
 * Libraries are stored in the resources/native/os.name/os.arch directory and copied
 * to the system temporary directory as indicated by the java.io.tmpdir property plus
 * a random one time directory name with the prefix "scopus_". In Linux, the temporary
 * directory would have a name like "/tmp/scopus_418af7c0b63b/". It should be
 */
object LibLoader {

  val tempPath = "scopus_"+UUID.randomUUID.toString.split("-").last
  val path = "native/" + getOsArch
  val destDir = System.getProperty("java.io.tmpdir") + "/" + tempPath + "/"
  new File(destDir).mkdirs() // Create the temporary directory

  def getOsArch = System.getProperty("os.name") + "/" + System.getProperty("os.arch")

  /**
   * Copy the OS dependent library from the resources dir in a JAR to a temporary location
   * and optionally ask the JVM to load the library. The library is deleted on exit from the
   * JVM.
   * @param libName The name of the library to copy and optionally load
   * @param load If true, ask the JVM to dynamically load the library using System.load()
   */
  def apply(libName: String, load: Boolean = true): Unit = {
    try {
      val source = Channels.newChannel(Opus.getClass.getClassLoader.getResourceAsStream(path + "/" + libName ))
      val fileOut = new File(destDir, libName )
      val dest = new FileOutputStream(fileOut)
      dest.getChannel.transferFrom(source,0,Long.MaxValue)
      source.close()
      dest.close()

      // Tee up deletion of the temporary library file when we quit
      sys.addShutdownHook {
        new File(destDir, libName).delete
        // Attempt to delete the directory also. It goes if the directory is empty
        new File(destDir).delete
      }
      // Finally, load the dynamic library if required
      if (load) System.load(fileOut.getAbsolutePath)
    } catch { // This is pretty catastrophic so bail.
      case e: Exception =>
        println("Fatal error in LibLoader: " + e.getMessage +" from class " + e.getClass.getCanonicalName)
        sys.exit(-1)
    }
  }
}

/**
 * Scala interface to the Opus codec API. With the exception of the *_ctl() commands, this
 * is pretty much a 1:1 mapping from Scala to the C API. See the Opus documentation for
 * details on these calls.
 */
object Opus {

  // System dependent load of native libraries
  LibLoader.getOsArch match {
    case "Linux/amd64" =>
      LibLoader("libopus.so.0",load = false) // Don't load this as it is dynamically found by the linker in Linux
      LibLoader("libjni_opus.so")
    case "Mac OS X/x86_64" =>
      LibLoader("libopus.0.dylib",load = false) // Don't load this as it is dynamically found by the linker in Linux
      LibLoader("libjni_opus.dylib")
    case s: String =>
        println(s"Unknown OS/platform combination: $s")
        sys.exit()
  }
  @native
  def decoder_create(Fs: Int, channels: Int, error: Array[Int]): Long

  @native
  def decode_short(decoder: Long, input: Array[Byte], inSize: Int, output: Array[Short], outSize: Int, decodeFEC: Int): Int

  @native
  def decode_float(decoder: Long, input: Array[Byte], inSize: Int, output: Array[Float], outSize: Int, decodeFEC: Int): Int

  @native
  def decoder_destroy(decoder: Long)

  @native
  def decoder_get_ctl(decoder: Long, command: Int, param: Array[Int]): Int

  @native
  def decoder_set_ctl(decoder: Long, command: Int, param: Int): Int

  @native
  def encoder_create(Fs: Int, channels: Int, application: Int, error: Array[Int]): Long

  @native
  def encode_short(encoder: Long, input: Array[Short], inSize: Int, output: Array[Byte], outSize: Int): Int

  @native
  def encode_float(encoder: Long, input: Array[Float], inSize: Int, output: Array[Byte], outSize: Int): Int

  @native
  def encoder_destroy(encoder: Long)

  @native
  def encoder_get_ctl(encoder: Long, command: Int, param: Array[Int]): Int

  @native
  def encoder_set_ctl(encoder: Long, command: Int, param: Int): Int

  @native
  def error_string(error: Int): String

  // Constants from opus.h
  final val OPUS_GET_LSB_DEPTH_REQUEST: Int = 4037
  final val OPUS_GET_APPLICATION_REQUEST: Int = 4001
  final val OPUS_GET_FORCE_CHANNELS_REQUEST: Int = 4023
  final val OPUS_GET_VBR_REQUEST: Int = 4007
  final val OPUS_GET_BANDWIDTH_REQUEST: Int = 4009
  final val OPUS_SET_BITRATE_REQUEST: Int = 4002
  final val OPUS_SET_BANDWIDTH_REQUEST: Int = 4008
  final val OPUS_SIGNAL_MUSIC: Int = 3002
  final val OPUS_RESET_STATE: Int = 4028
  final val OPUS_MULTISTREAM_GET_DECODER_STATE_REQUEST: Int = 5122
  final val OPUS_FRAMESIZE_2_5_MS: Int = 5001
  final val OPUS_GET_COMPLEXITY_REQUEST: Int = 4011
  final val OPUS_FRAMESIZE_40_MS: Int = 5005
  final val OPUS_SET_PACKET_LOSS_PERC_REQUEST: Int = 4014
  final val OPUS_GET_VBR_CONSTRAINT_REQUEST: Int = 4021
  final val OPUS_SET_INBAND_FEC_REQUEST: Int = 4012
  final val OPUS_APPLICATION_RESTRICTED_LOWDELAY: Int = 2051
  final val OPUS_BANDWIDTH_FULLBAND: Int = 1105
  final val OPUS_SET_VBR_REQUEST: Int = 4006
  final val OPUS_BANDWIDTH_SUPERWIDEBAND: Int = 1104
  final val OPUS_SET_FORCE_CHANNELS_REQUEST: Int = 4022
  final val OPUS_APPLICATION_VOIP: Int = 2048
  final val OPUS_SIGNAL_VOICE: Int = 3001
  final val OPUS_GET_FINAL_RANGE_REQUEST: Int = 4031
  final val OPUS_BUFFER_TOO_SMALL: Int = -2
  final val OPUS_SET_COMPLEXITY_REQUEST: Int = 4010
  final val OPUS_FRAMESIZE_ARG: Int = 5000
  final val OPUS_GET_LOOKAHEAD_REQUEST: Int = 4027
  final val OPUS_GET_INBAND_FEC_REQUEST: Int = 4013
  final val OPUS_BITRATE_MAX: Int = -1
  final val OPUS_FRAMESIZE_5_MS: Int = 5002
  final val OPUS_BAD_ARG: Int = -1
  final val OPUS_GET_PITCH_REQUEST: Int = 4033
  final val OPUS_SET_SIGNAL_REQUEST: Int = 4024
  final val OPUS_FRAMESIZE_20_MS: Int = 5004
  final val OPUS_APPLICATION_AUDIO: Int = 2049
  final val OPUS_MULTISTREAM_GET_ENCODER_STATE_REQUEST: Int = 5120
  final val OPUS_GET_DTX_REQUEST: Int = 4017
  final val OPUS_FRAMESIZE_10_MS: Int = 5003
  final val OPUS_SET_LSB_DEPTH_REQUEST: Int = 4036
  final val OPUS_UNIMPLEMENTED: Int = -5
  final val OPUS_GET_PACKET_LOSS_PERC_REQUEST: Int = 4015
  final val OPUS_INVALID_STATE: Int = -6
  final val OPUS_SET_EXPERT_FRAME_DURATION_REQUEST: Int = 4040
  final val OPUS_FRAMESIZE_60_MS: Int = 5006
  final val OPUS_GET_BITRATE_REQUEST: Int = 4003
  final val OPUS_INTERNAL_ERROR: Int = -3
  final val OPUS_SET_MAX_BANDWIDTH_REQUEST: Int = 4004
  final val OPUS_SET_VBR_CONSTRAINT_REQUEST: Int = 4020
  final val OPUS_GET_MAX_BANDWIDTH_REQUEST: Int = 4005
  final val OPUS_BANDWIDTH_NARROWBAND: Int = 1101
  final val OPUS_SET_GAIN_REQUEST: Int = 4034
  final val OPUS_SET_PREDICTION_DISABLED_REQUEST: Int = 4042
  final val OPUS_SET_APPLICATION_REQUEST: Int = 4000
  final val OPUS_SET_DTX_REQUEST: Int = 4016
  final val OPUS_BANDWIDTH_MEDIUMBAND: Int = 1102
  final val OPUS_GET_SAMPLE_RATE_REQUEST: Int = 4029
  final val OPUS_GET_EXPERT_FRAME_DURATION_REQUEST: Int = 4041
  final val OPUS_AUTO: Int = -1000
  final val OPUS_GET_SIGNAL_REQUEST: Int = 4025
  final val OPUS_GET_LAST_PACKET_DURATION_REQUEST: Int = 4039
  final val OPUS_GET_PREDICTION_DISABLED_REQUEST: Int = 4043
  final val OPUS_GET_GAIN_REQUEST: Int = 4045
  final val OPUS_BANDWIDTH_WIDEBAND: Int = 1103
  final val OPUS_INVALID_PACKET: Int = -4
  final val OPUS_ALLOC_FAIL: Int = -7
  final val OPUS_OK: Int = 0
}
