(ns mui.icons
  (:require
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    #?@(:cljs [["@mui/icons-material/ChevronRight" :default ChevronRightIcon]])))

(def chevron-right (interop/react-factory #?(:cljs ChevronRightIcon :clj nil)))
