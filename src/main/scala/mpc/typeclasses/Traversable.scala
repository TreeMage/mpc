package org.treemage
package mpc.typeclasses

trait Traversable[F[_]] extends Foldable[F] with Functor[F]:
  def traverse[A, B, G[_]: Applicative](fa: F[A])(f: A => G[B]): G[F[B]]
  def sequence[A, G[_]: Applicative](fga: F[G[A]]): G[F[A]] =
    traverse(fga)(identity)

object Traversable:
  def apply[F[_]: Traversable]: Traversable[F] = summon[Traversable[F]]

  extension [F[_]: Traversable, A](self: F[A])
    def traverse[B, G[_]: Applicative](f: A => G[B]): G[F[B]] =
      Traversable[F].traverse(self)(f)

  extension [F[_]: Traversable, G[_]: Applicative, A](self: F[G[A]])
    def sequence: G[F[A]] =
      Traversable[F].sequence(self)
