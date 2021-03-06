package scalafix.internal.cli

import java.io.File
import scala.meta.Input

// a file that is supposed to be run through scalafix.
case class FixFile(
    // The file on the local filesystem where the fix should written to.
    original: Input.File,
    // For semantic rules on fat semanticdb, the input in scalafix.SemanticdbIndex
    // is labeled strings instead of Input.File. The labeled string must be used
    // in the RuleCtx in order to position lookups in SemanticdbIndex.names/symbols
    // to match, since scala.meta.Position.input must match.
    semanticFile: Option[Input.VirtualFile] = None,
    // Was this file passed explicitly or expanded from a directory?
    // If the file was expanded from a directory, we may want to skip reporting
    // a parse error.
    passedExplicitly: Boolean = false
) {
  override def toString: String =
    s"InputFile(${original.path.toNIO}, $semanticFile, $passedExplicitly)"
  def toParse: Input = semanticFile.getOrElse(original)
  def toIO: File = original.path.toFile
}
