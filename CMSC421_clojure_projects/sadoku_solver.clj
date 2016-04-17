
(use 'clojure.core.logic)

(defn parento
  "A relation where p is the parent of c."
  [p c]
  (conde
    [(== p :antonio) (== c :vito)]
    [(== p :vito) (== c :michael)]
    [(== p :vito) (== c :sonny)]
    [(== p :vito) (== c :fredo)]
    [(== p :michael) (== c :mary)]
    [(== p :michael) (== c :anthony)]))

(defn grandparento
  "A relation where gp is the grandparent of gc."
  [gp gc]
  (fresh [mid]
    (parento mid gc)
    (parento gp mid)
  )
)

(defn ancestoro
  "A relation where a is the ancestor of b."
  [a b]
  (fresh [c]
    (conde
      [(parento a b)]
      [(grandparento a b)]
      [(grandparento c b)(ancestoro a c)]
    )
  )
)

(defn family-data
  "Return a map of information about person.
  The keys are :ancestors, :descendants, :grandparents, :grandchildren.
  The values are all collections."
  [person]
  {:ancestors (run* [q] (ancestoro q person)),
   :descendants (run* [q] (ancestoro person q)),
   :grandparents (run* [q] (grandparento q person)),
   :grandchildren (run* [q] (grandparento person q))
  })

(defn rpslso
  "A relation where p1 beats p2 in rock-paper-scissors-lizard-spock.
  This can be done with (conde), but (matche) may save you some typing:
  https://github.com/frenchy64/Logic-Starter/wiki#wiki-matche"
  [p1 p2]
  (conde
    [(== p1 :rock) (== p2 :scissors)]
    [(== p1 :rock) (== p2 :lizard)]
    [(== p1 :scissors) (== p2 :paper)]
    [(== p1 :scissors) (== p2 :lizard)]
    [(== p1 :paper) (== p2 :rock)]
    [(== p1 :paper) (== p2 :spock)]
    [(== p1 :spock) (== p2 :rock)]
    [(== p1 :spock) (== p2 :scissors)]
    [(== p1 :lizard) (== p2 :spock)]
    [(== p1 :lizard) (== p2 :paper)]
  )
)

(defn sub-triangle
  "Return a collection of vectors of length 4, each a chain of winning moves
  that start and end with gesture. e.g. [:spock :scissors :paper :spock]."
  [gesture]
  (run* [d]
    (fresh [a b c]
    (== gesture a)
    (rpslso a b)
    (rpslso b c)
    (rpslso c a)
    (== d [a, b, c, a])
    )))

(def symbols
  "Legal sudoku symbols."
  [:a :b :c :d])


(defn subsudoku
  [a1 a2 b1 b2]
  (conde
    [(== a1 :a)(== a2 :b)(== b1 :c)(== b2 :d)]
    [(== a1 :a)(== a2 :b)(== b1 :d)(== b2 :c)]
    [(== a1 :a)(== a2 :c)(== b1 :b)(== b2 :d)]
    [(== a1 :a)(== a2 :c)(== b1 :d)(== b2 :b)]
    [(== a1 :a)(== a2 :d)(== b1 :b)(== b2 :c)]
    [(== a1 :a)(== a2 :d)(== b1 :c)(== b2 :b)]

    [(== a1 :b)(== a2 :a)(== b1 :c)(== b2 :d)]
    [(== a1 :b)(== a2 :a)(== b1 :d)(== b2 :c)]
    [(== a1 :b)(== a2 :c)(== b1 :a)(== b2 :d)]
    [(== a1 :b)(== a2 :c)(== b1 :d)(== b2 :a)]
    [(== a1 :b)(== a2 :d)(== b1 :a)(== b2 :c)]
    [(== a1 :b)(== a2 :d)(== b1 :c)(== b2 :a)]

    [(== a1 :c)(== a2 :a)(== b1 :b)(== b2 :d)]
    [(== a1 :c)(== a2 :a)(== b1 :d)(== b2 :b)]
    [(== a1 :c)(== a2 :b)(== b1 :a)(== b2 :d)]
    [(== a1 :c)(== a2 :b)(== b1 :d)(== b2 :a)]
    [(== a1 :c)(== a2 :d)(== b1 :a)(== b2 :b)]
    [(== a1 :c)(== a2 :d)(== b1 :b)(== b2 :a)]

    [(== a1 :d)(== a2 :a)(== b1 :b)(== b2 :c)]
    [(== a1 :d)(== a2 :a)(== b1 :c)(== b2 :b)]
    [(== a1 :d)(== a2 :b)(== b1 :a)(== b2 :c)]
    [(== a1 :d)(== a2 :b)(== b1 :c)(== b2 :a)]
    [(== a1 :d)(== a2 :c)(== b1 :a)(== b2 :b)]
    [(== a1 :d)(== a2 :c)(== b1 :b)(== b2 :a)]
  )
)

(defn sudokuo
  "A relation where a1...d4 is a valid mini-sudoku board."
  [a4 b4 c4 d4
   a3 b3 c3 d3
   a2 b2 c2 d2
   a1 b1 c1 d1]
  (all
  (subsudoku a1 a2 a3 a4)(subsudoku b1 b2 b3 b4)
  (subsudoku c1 c2 c3 c4)(subsudoku d1 d2 d3 d4)
  (subsudoku a1 b1 c1 d1)(subsudoku a2 b2 c2 d2)
  (subsudoku a3 b3 c3 d3)(subsudoku a4 b4 c4 d4)
  (subsudoku a1 b1 a2 b2)(subsudoku c1 d1 c2 d2)
  (subsudoku a3 b3 a4 b4)(subsudoku c3 d3 c4 d4))
)

(defn print-sudoku
  "Print n solutions to mini-sudoku. (Use to test your sudokuo relation.)"
  [n]
  (doall
    (map println
         (run n [q]
           (fresh [a4 b4 c4 d4 a3 b3 c3 d3 a2 b2 c2 d2 a1 b1 c1 d1]
             (sudokuo a4 b4 c4 d4
                      a3 b3 c3 d3
                      a2 b2 c2 d2
                      a1 b1 c1 d1)
             (== q [[a4 b4 c4 d4]
                    [a3 b3 c3 d3]
                    [a2 b2 c2 d2]
                    [a1 b1 c1 d1]])))))
  nil)