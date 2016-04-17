;; Mark Penny
;; jmpenny

;; Sample configuration in blocks world:
;; 
;;  :a 
;;  :b :d
;;  :c :e
;; :table
;; 
;; (def initial-pos {:a :b, :b :c, :c :table, :d :e, :e :table})
;; (def initial-state (init initial-pos))

(defn init
  "Returns an appropriate initial state as specified by pos.
   By default, the arm is empty."
  [pos]
  (let [holding nil
        non-clear (set (vals pos))
        clear (set
                (remove #(contains? non-clear %)
                        (keys pos)))]

    {:pos pos, :holding holding, :clear clear}))

(defn pickup
  "Returns state resulting from picking up block, nil if pickup is illegal.
   Preconditions: block is clear, arm is empty.
   Postconditions: arm is holding block, block is not clear,
                   block-below (if any) is now clear. pos of block is nil."
  [state block]
  (cond (:holding state) nil
        (not ((:clear state) block)) nil
        :else 
          (let [holding block
                pos (assoc (:pos state) block nil)
                block-below ((:pos state) block)
                clear (if (= block-below :table)
                          (-> (:clear state) (disj block))
                          (-> (:clear state) (disj block)
                              (conj block-below)))]

            {:pos pos, :holding holding, :clear clear})))

(defn puton
  "Returns state resulting from putting currently held block on target.
   Returns nil if puton is an illegal action.
   Preconditions: arm is holding block, target is :table or clear.
   Postconditions: arm is empty, target is not clear, block is clear,
                   block is on top of target."
  [state target]
  (cond (not (:holding state)) nil
        (and (not ((:clear state) target)) (not= target :table)) nil
        :else
          (let [block (:holding state)
                holding nil
                pos (assoc (:pos state) block target)
                clear (-> (:clear state) (disj target) (conj block))]

            {:pos pos, :holding holding, :clear clear})))

(defn get-on-set
  "returns a vector of all elements that are not on the table"
  [state, sol]
  (if (== (count state) 0)
    sol
    (if (= (last (first state)) :table) 
      (get-on-set (rest state) sol)
      (get-on-set (rest state) (conj sol (first (first state))))
    )
  )
)

(defn move-one-to-table
  [clear-set on-set]
  (let [f 
  (for [x on-set]
    (if (=  (first clear-set) x)
      [`(pickup ~(first clear-set)) '(puton :table)]
    )
  )] 
    (if (= () (filter identity f))
      (move-one-to-table (rest clear-set) on-set)
      (first (filter identity f))
    )
  )
)

(defn apply-plan
  "Returns the result of applying, in sequence, every action in actions to
   state. e.g. (apply-actions state ['(pickup :a) '(puton :table)]) returns
   the equivalent of (puton (pickup state :a) :table)."
  [state actions]
  (eval (concat `(-> ~state) actions)))


(defn move-all-to-table
  "Recursively move all of the boxes to the table"
  [imy2 rv]
  (let [ n (get-on-set (get imy2 :pos) [])]
  (if (== (count n) 0)
    rv
    (let [instr (move-one-to-table (get imy2 :clear) n)]
    (move-all-to-table  
      (apply-plan imy2 instr)
      (concat rv instr)
    )
    )
  )
  )
)

(defn correct-set
  "Takes position list and a target list and returns what is in the correct place"
  [curr target ]
  (filter identity  ;; Remove nil
  (apply concat     ;; Partial flatten
  (for [x curr]
    (for [y target]
      (if (= (first x) (first y))
        (if (= (last x) (last y))
          x 
        )
      )
    )
  )
  )
  )
)

(defn what-goes-on-base-case
  [goal correct-now]
  (if (== 1 (count correct-now))
  (= (count goal) (get (frequencies 
  (let [n (first correct-now)]
    (for [x goal]
      (if (= (last x) (first n))
        true nil
      )
    )
  )) nil)
  )
  false
  )
)

(defn what-goes-on
  [goal correct-now rv]
  (if (== 0 (count correct-now)) rv 
  (if (what-goes-on-base-case goal correct-now) rv
  (let [n (first correct-now)]
    (filter identity (apply concat
    (for [x goal]
      (if (= (last x) (first n))
        (filter identity (apply concat (what-goes-on goal
          (conj (rest correct-now) [(first x) (first n)])
          (conj rv [`(pickup ~(first x)) `(puton ~(first n))])) 
        nil
        ))
      )
    )
    )
    )
  )
  )
  )
)

(defn make-2-list
  [l]
  (if (== 0 (count l))
    []
    (cons (list (first l) (second l)) (make-2-list (rest (rest l))))
  )
)

(defn move-all-to-goal-state
  "recursively move all of the boxes to the goal state"
  [state goal]
  (make-2-list (flatten
  (for [x (correct-set (get state :pos) goal)]
   (reverse (what-goes-on goal (list x) nil))
  )))
)

(defn reached-goal?
  "Returns true iff state satsifies goal."
  [state goal]
  (every? true? (for [k (keys goal)]
                  (= ((:pos state) k) (goal k)))))

(defn apply-plan
  "Returns the result of applying, in sequence, every action in actions to
   state. e.g. (apply-actions state ['(pickup :a) '(puton :table)]) returns
   the equivalent of (puton (pickup state :a) :table)."
  [state actions]
  (eval (concat `(-> ~state) actions)))

;;;;;;;;

(defn find-plan
  "Finds a plan from start-pos to goal.
   TODO: Write this function!"
  [start-pos goal]
  (let [t (move-all-to-table (init start-pos) ())]
    (concat t (move-all-to-goal-state (apply-plan (init start-pos) t) goal))
  )
)

;;;; TESTS ;;;;

;;; Write your own tests. Tests are good!
;;; (I've included tests directly in this file in the hopes that more people
;;; will use them for this project.)

(def tall {:a :b, :b :c, :c :d, :d :e,
           :e :f, :f :g, :g :h, :h :i, :i :table})
(def tri {:a :b, :b :c, :c :table, :d :e, :e :f, :f :table}) 
(def goal-small {:c :a, :a :b, :b :table})
(def goal-large {:c :a, :a :b, :b :e, :e :table, :d :f, :f :table})

(def my {:c :d, :b :a, :d :table, :a :table})
(def my-o-t {:a :table, :b :table, :c :table, :d :table})
(def my-goal {:d :table, :c :b, :b :a, :a :table})

(defn test-find-plan
  "True if the plan from find-plan succesfully reaches the goal."
  [start-pos goal]
  (reached-goal? (apply-plan (init start-pos) (find-plan start-pos goal))
                 goal))

(defn run-tests []
  (do
    (println "Running tests...")
    (println (test-find-plan tall goal-small)
             (test-find-plan tall goal-large)
             (test-find-plan tri goal-small)
             (test-find-plan tri goal-large)
	     (test-find-plan my my-goal))))

;; user=> (time (run-tests))
;; Running tests...
;; true true true true
;; "Elapsed time: 59.365189 msecs"
;; nil
;;

