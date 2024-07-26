package org.treemage
package mpc.typeclasses

trait Monad[F[_]] extends Applicative[F]:
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

  override def ap[A, B](fa: F[A])(ff: F[A => B]): F[B] =
    flatMap(fa)(a => flatMap(ff)(f => pure(f(a))))

object Monad:
  def apply[F[_]: Monad]: Monad[F] = summon[Monad[F]]
  
  extension [A, F[_]](self: F[A])(using F: Monad[F])
    def flatMap[B](f: A => F[B]): F[B] = F.flatMap(self)(f)

  extension [F[_], A](self: F[F[A]])(using F: Monad[F])
    def flatten: F[A] =
      self.flatMap(fa => fa.flatMap(F.pure))
