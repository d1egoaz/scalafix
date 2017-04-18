package scalafix
package rewrite

import scala.collection.immutable.Seq
import scala.meta._
import scalafix.util.Patch
import scala.collection.immutable.Seq
import scalafix.config.ReaderUtil

/** A rewrite is a named RewriteCtx[A] => Seq[Patch] function.
  * @tparam A Required api in [[ScalafixMirror]]. Example values:
  *           [[ScalafixMirror]] for scalafix-nsc,
  *           [[scala.meta.Mirror]] when using scalahost or
  *           [[Any]] for syntactic rewrites.
  */
abstract class Rewrite[-A](implicit sourceName: sourcecode.Name) {
  def name: String = sourceName.value
  override def toString: String = name
  def rewrite[B <: A](ctx: RewriteCtx[B]): Patch
  def andThen[B <: A](other: Rewrite[B]): Rewrite[B] =
    Rewrite(ctx => this.rewrite(ctx) + other.rewrite(ctx))
}

object Rewrite {
  def empty[T]: Rewrite[T] = syntactic(_ => Patch.empty)
  def syntactic(f: SyntacticRewriteCtx => Patch)(
      implicit name: sourcecode.Name): SyntaxRewrite = apply(f)
  def semantic(f: SemanticRewriteCtx => Patch)(
      implicit name: sourcecode.Name): SemanticRewrite = apply(f)
  def apply[T](f: RewriteCtx[T] => Patch)(
      implicit name: sourcecode.Name): Rewrite[T] = new Rewrite[T]() {
    override def rewrite[B <: T](ctx: RewriteCtx[B]): Patch = f(ctx)
  }
}
