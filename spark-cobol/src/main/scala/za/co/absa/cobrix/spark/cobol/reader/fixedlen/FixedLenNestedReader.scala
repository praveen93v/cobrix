/*
 * Copyright 2018 Barclays Africa Group Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package za.co.absa.cobrix.spark.cobol.reader.fixedlen

import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
import za.co.absa.cobrix.cobol.parser.CopybookParser
import za.co.absa.cobrix.cobol.parser.encoding.EBCDIC
import za.co.absa.cobrix.spark.cobol.reader.fixedlen.iterator.FixedLenNestedRowIterator
import za.co.absa.cobrix.spark.cobol.schema.CobolSchema

/** The Cobol data reader that produces nested structure schema */
class FixedLenNestedReader(val copyBookContents: String,
                           startOffset: Int = 0,
                           endOffset: Int = 0) extends FixedLenReader with Serializable {

  private val cobolSchema: CobolSchema = loadCopyBook(copyBookContents)

  override def getCobolSchema: CobolSchema = cobolSchema
  override def getSparkSchema: StructType = cobolSchema.getSparkSchema

  @throws(classOf[Exception])
  override def getRowIterator(binaryData: Array[Byte]): Iterator[Row] = {
    checkBinaryDataValidity(binaryData)
    new FixedLenNestedRowIterator(binaryData, cobolSchema, startOffset, endOffset)
  }

  @throws(classOf[IllegalArgumentException])
  private def checkBinaryDataValidity(binaryData: Array[Byte]): Unit = {
    if (startOffset < 0) {
      throw new IllegalArgumentException (s"Invalid record start offset = $startOffset. A record start offset cannot be negative.")
    }
    if (endOffset < 0) {
      throw new IllegalArgumentException (s"Invalid record end offset = $endOffset. A record end offset cannot be negative.")
    }
    if (binaryData.length < getExpectedLength) {
      throw new IllegalArgumentException (s"Binary record too small. Expected binary record size = $getExpectedLength, got ${binaryData.length} ")
    }
    if (binaryData.length % getExpectedLength > 0) {
      throw new IllegalArgumentException (s"Binary record size $getExpectedLength does not divide data size ${binaryData.length}.")
    }
  }

  private def getExpectedLength: Int = {
    cobolSchema.getRecordSize + startOffset + endOffset
  }

  private def loadCopyBook(copyBookContents: String): CobolSchema = {
    val schema = CopybookParser.parseTree(EBCDIC(), copyBookContents)
    new CobolSchema(schema)
  }

  override def getRecordStartOffset: Int = startOffset

  override def getRecordEndOffset: Int = endOffset
}