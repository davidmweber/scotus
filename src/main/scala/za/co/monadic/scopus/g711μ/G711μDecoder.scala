/*
 * Copyright © ${year} 8eo Inc.
 */
package za.co.monadic.scopus.g711μ

import za.co.monadic.scopus.{DecoderFloat, DecoderShort, SampleFrequency, Sf8000}

import scala.util.{Success, Try}

private object G711μDecoder {

  // Lookup table for μ-law decoder
  val μToLin: Array[Short] = Array[Short](-32124, -31100, -30076, -29052, -28028, -27004, -25980, -24956, -23932,
    -22908, -21884, -20860, -19836, -18812, -17788, -16764, -15996, -15484, -14972, -14460, -13948, -13436, -12924,
    -12412, -11900, -11388, -10876, -10364, -9852, -9340, -8828, -8316, -7932, -7676, -7420, -7164, -6908, -6652, -6396,
    -6140, -5884, -5628, -5372, -5116, -4860, -4604, -4348, -4092, -3900, -3772, -3644, -3516, -3388, -3260, -3132,
    -3004, -2876, -2748, -2620, -2492, -2364, -2236, -2108, -1980, -1884, -1820, -1756, -1692, -1628, -1564, -1500,
    -1436, -1372, -1308, -1244, -1180, -1116, -1052, -988, -924, -876, -844, -812, -780, -748, -716, -684, -652, -620,
    -588, -556, -524, -492, -460, -428, -396, -372, -356, -340, -324, -308, -292, -276, -260, -244, -228, -212, -196,
    -180, -164, -148, -132, -120, -112, -104, -96, -88, -80, -72, -64, -56, -48, -40, -32, -24, -16, -8, 0, 32124,
    31100, 30076, 29052, 28028, 27004, 25980, 24956, 23932, 22908, 21884, 20860, 19836, 18812, 17788, 16764, 15996,
    15484, 14972, 14460, 13948, 13436, 12924, 12412, 11900, 11388, 10876, 10364, 9852, 9340, 8828, 8316, 7932, 7676,
    7420, 7164, 6908, 6652, 6396, 6140, 5884, 5628, 5372, 5116, 4860, 4604, 4348, 4092, 3900, 3772, 3644, 3516, 3388,
    3260, 3132, 3004, 2876, 2748, 2620, 2492, 2364, 2236, 2108, 1980, 1884, 1820, 1756, 1692, 1628, 1564, 1500, 1436,
    1372, 1308, 1244, 1180, 1116, 1052, 988, 924, 876, 844, 812, 780, 748, 716, 684, 652, 620, 588, 556, 524, 492, 460,
    428, 396, 372, 356, 340, 324, 308, 292, 276, 260, 244, 228, 212, 196, 180, 164, 148, 132, 120, 112, 104, 96, 88, 80,
    72, 64, 56, 48, 40, 32, 24, 16, 8, 0)

  // Lookup table returning Float values
  val μToLinF: Array[Float] = new Array[Float](μToLin.length)

  // Just build the Float version
  for (i ← μToLin.indices) {
    μToLinF(i) = μToLin(i) / 32124.0f
  }

}

case class G711μDecoderShort(fs: SampleFrequency, channels: Int) extends DecoderShort {

  import G711μDecoder.μToLin

  /**
    * Decode an audio packet to an array of Shorts
    *
    * @param compressedAudio The incoming audio packet
    * @return A Try containing decoded audio in Short format
    */
  override def apply(compressedAudio: Array[Byte]): Try[Array[Short]] = {
    val out = new Array[Short](compressedAudio.length)
    var i   = 0
    while (i < compressedAudio.length) {
      out(i) = μToLin(compressedAudio(i) & 0xff)
      i += 1
    }
    Success(out)
  }

  /**
    * Decode an erased (i.e. not received) audio packet. Note you need to specify
    * how many samples you think you have lost so the decoder can attempt to
    * deal with the erasure appropriately.
    *
    * @return A Try containing decompressed audio in Float format
    */
  override def apply(count: Int): Try[Array[Short]] = Try(new Array[Short](count))

  /**
    * Release all pointers allocated for the encoder. Make every attempt to call this
    * when you are done with the encoder as finalise() is what it is in the JVM
    */
  override def cleanup(): Unit = ()

  /**
    * @return A description of this instance of an encoder or decoder
    */
  override def getDetail: String = "G.711u μ-law decoder"

  /**
    * Reset the underlying codec.
    */
  override def reset: Int = 0

  /**
    * @return The sample rate for this codec's instance
    */
  override def getSampleRate: Int = Sf8000()
}

case class G711μDecoderFloat(fs: SampleFrequency, channels: Int) extends DecoderFloat {

  import G711μDecoder.μToLinF

  /**
    * Decode an audio packet to an array of Floats
    *
    * @param compressedAudio The incoming audio packet
    * @return A Try containing the decoded audio packet in Float format
    */
  override def apply(compressedAudio: Array[Byte]): Try[Array[Float]] = {
    val out = new Array[Float](compressedAudio.length)
    var i   = 0
    while (i < compressedAudio.length) {
      out(i) = μToLinF(compressedAudio(i) & 0xff)
      i += 1
    }
    Success(out)
  }

  /**
    * Decode an erased (i.e. not received) audio packet. Note you need to specify
    * how many samples you think you have lost so the decoder can attempt to
    * deal with the erasure appropriately.
    *
    * @return A Try containing decompressed audio in Float format
    */
  override def apply(count: Int): Try[Array[Float]] = Try(new Array[Float](count))

  /**
    * Release all pointers allocated for the encoder. Make every attempt to call this
    * when you are done with the encoder as finalise() is what it is in the JVM
    */
  override def cleanup(): Unit = ()

  /**
    * @return A discription of this instance of an encoder or decoder
    */
  override def getDetail: String = "G.711u μ-law decoder"

  /**
    * Reset the underlying codec.
    */
  override def reset: Int = 0

  /**
    * @return The sample rate for this codec's instance
    */
  override def getSampleRate: Int = fs()
}
