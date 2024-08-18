package org.treemage
package mpc.util

import mpc.typeclasses.{Traversable, Applicative}

object ListTypeClasses:
  given Traversable[List] = new Traversable[List]:
    override def traverse[A, B, G[_]: Applicative](fa: List[A])(
        f: A => G[B]
    ): G[List[B]] =
      foldRight(fa, Applicative[G].pure(List.empty[B])) { case (cur, acc) =>
        Applicative[G].map2(f(cur), acc)(_ +: _)
      }

    override def foldRight[A, B](fa: List[A], zero: => B)(
        combine: (A, B) => B
    ): B = fa.foldRight(zero)(combine)

    override def map[A, B](fa: List[A], f: A => B): List[B] = fa.map(f)
