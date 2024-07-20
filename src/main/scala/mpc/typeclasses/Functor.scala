package org.treemage
package mpc.typeclasses

import scala.annotation.targetName

trait Functor[F[_]]:
  def map[A, B](fa: F[A], f: A => B): F[B]

object Functor:
  extension [A, F[_]](self: F[A])(using F: Functor[F])
    @targetName("infixMap")
    def `<$>`[B](f: A => B): F[B] = self.map(f)

    def map[B](f: A => B): F[B] = F.map(self, f)
