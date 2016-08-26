(ns imin.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [imin.core-test]
   [imin.common-test]))

(enable-console-print!)

(doo-tests 'imin.core-test
           'imin.common-test)
