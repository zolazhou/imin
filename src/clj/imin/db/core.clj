(ns imin.db.core
  (:require [conman.core :as conman]))

(defonce ^:dynamic *db* (conman/connect! {:jdbc-url "jdbc:h2:./h2.db"}))

(conman/bind-connection *db* "sql/queries.sql")



(create-user! {:username "zola"
               :email    "zolazhou@gmail.com"
               :alipay   "zolazhou@gmail.com"})

