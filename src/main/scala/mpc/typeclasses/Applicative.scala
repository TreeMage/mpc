package org.treemage
package mpc.typeclasses

import scala.annotation.targetName

trait Applicative[F[_]] extends Functor[F]:
  def pure[A](x: A): F[A]
  def ap[A, B](fa: F[A])(f: F[A => B]): F[B]

  override def map[A, B](fa: F[A], f: A => B): F[B] = ap(fa)(pure(f))
  def map2[A, B, C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] =
    ap(fa)(ap(fb)(pure(b => a => f(a, b))))

object Applicative:
  def apply[F[_]: Applicative]: Applicative[F] = summon[Applicative[F]]
  
  extension [F[_], A](self: F[A])(using F: Applicative[F])
    @targetName("zip")
    def <*>[B](fb: F[B]): F[(A, B)] = F.map2(self, fb)((_, _))

    @targetName("zipLeft")
    def <*[B](fb: F[B]): F[A] = F.map2(self, fb)((a, _) => a)

    @targetName("zipRight")
    def *>[B](fb: F[B]): F[B] = F.map2(self, fb)((_, b) => b)
