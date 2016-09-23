(ns imin.parser)

;; read

(defmulti readf (fn [_ k _] k))


;; mutate

(defmulti mutatef (fn [_ k _] k))
