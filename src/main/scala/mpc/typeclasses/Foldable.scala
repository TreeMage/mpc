package org.treemage
package mpc.typeclasses

trait Foldable[F[_]]:
  def foldRight[A, B](fa: F[A], zero: => B)(combine: (A, B) => B): B

object Foldable:
  extension [F[_], A](self: F[A])(using F: Foldable[F])
    def foldRight[B](zero: => B)(combine: (A, B) => B): B =
      F.foldRight(self, zero)(combine)
