(ns se.w3t.site.fontawesome.icons
  (:require [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            ["@fortawesome/react-fontawesome" :default FontAwesomeIcon]))

(def icon (interop/react-factory FontAwesomeIcon))
