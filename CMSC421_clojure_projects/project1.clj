;;;; Mark Penny Section 0101

(defn qextends?row
  "Returns true if a queen is in an empty row."
  [partial-sol rank]
  (if (> (count partial-sol) 0)
    (if (= (partial-sol (- (count partial-sol) 1)) rank)
      false
      (qextends?row (pop partial-sol) rank)
    )
    true
  )
)

(defn qextends?diagup
  "Returns true if there are no queens up and right"
  [partial-sol rank diff]
  (if (> (count partial-sol) 0)
    (if (= (partial-sol (- (count partial-sol) 1)) (+ rank diff))
      false
      (qextends?diagup (pop partial-sol) rank (+ diff 1))
    )
    true
  )
)

(defn qextends?diagdown
  "Returns true if there are no queens down and right"
  [partial-sol rank diff]
  (if (> (count partial-sol) 0)
    (if (> (- rank diff) 0 )
      (if (= (partial-sol (- (count partial-sol) 1)) (- rank diff))
        false
        (qextends?diagdown (pop partial-sol) rank (+ diff 1))
      )
      true
    )
    true
  )
)

(defn qextends?
  "Returns true if a queen at rank extends partial-sol."
  [partial-sol rank]
  (if (qextends?row partial-sol rank)
    (if (qextends?diagup partial-sol rank 1)
      (if (qextends?diagdown partial-sol rank 1)
        true
        false
      )
      false
    )
    false
  )
)

(defn qextendSingle
  "Given a single partial solution vector and the size of the board,
  returns all possible solutions of size k+1"
  [n partial-sol returnVal]
  (if (> n 0)
    (if (qextends? partial-sol n)
      (qextendSingle (- n 1) partial-sol (conj returnVal (conj partial-sol n)))
      (qextendSingle (- n 1) partial-sol returnVal)
    )
    returnVal
  )
)

(defn qextend2
  "Given a vector *partial-sol-list* of all partial solutions of length k,
  returns a vector of all partial solutions of length k + 1."
  [n partial-sol-list returnVal]
  (if (< 0 (count partial-sol-list))
    (
      qextend2 n (pop partial-sol-list)
      ;;Recurse on partial-sol-list with the last element removed
      (qextendSingle n (nth partial-sol-list (- (count partial-sol-list) 1)) returnVal)
      ;;Call qextendSingle on the last element of partial-sol-list, add to result vector
    )
    returnVal
  )
)

(defn qextend
  "Given a vector *partial-sol-list* of all partial solutions of length k,
  returns a vector of all partial solutions of length k + 1."
  [n partial-sol-list]
  (qextend2 n partial-sol-list [])
)

(defn sol-count2
  "returns the total number of n-queen solutions on an n x n board."
  [n current]
  (def returnVal 0)
  (dotimes [i n]
    (if (= (count current) (- n 1))
      (if (qextends? current (+ 1 i)) 
        (def returnVal (+ returnVal 1)))
      (if (qextends? current (+ 1 i))
        (def returnVal (+ returnVal (sol-count2 n (conj current (+ 1 i)))))
      )
    )
  )
  returnVal
)

(defn sol-count
  "Returns the total number of n-queens solutions on an n x n board."
  [n]
  (sol-count2 n [])
)

(defn exp "x^n" [x n] 
  (reduce * (repeat n x)))

(defn sol-density
  "Return the density of solutions on an n x n board."
  [n]
  (/(sol-count n) (exp n n))
)
