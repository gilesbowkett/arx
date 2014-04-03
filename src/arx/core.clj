(ns arx.core
  (:use overtone.live))

; drum sounds (sampled)
(def snare (sample (freesound-path 26903)))
(def kick (sample (freesound-path 2086)))

; drum sound (synthesized)
(definst hat [volume 1.0]
  (let [src (white-noise)
        env (env-gen (perc 0.001 0.1) :action FREE)]
    (* volume 1 src env)))

; volume modified
(defn weak-hat []
  (hat 0.3))

; metronome
(def metro (metronome 110))

(defn kicks [metro beat-number]
  (at (metro (+ 0 beat-number)) (kick))
  (at (metro (+ 1 beat-number)) (kick))
  (at (metro (+ 2 beat-number)) (kick))
  (at (metro (+ 3 beat-number)) (kick)))

; FIXME: DRY
(defn snares [metro beat-number]
  (at (metro (+ 0.75 beat-number)) (snare))
  (at (metro (+ 1.5 beat-number)) (snare))
  (at (metro (+ 2.75 beat-number)) (snare))
  (at (metro (+ 3.5 beat-number)) (snare)))

; FIXME: DRY
(defn hats [metro beat-number]
  (at (metro (+ 0.5 beat-number)) (weak-hat))
  (at (metro (+ 1.5 beat-number)) (weak-hat))
  (at (metro (+ 2.5 beat-number)) (weak-hat))
  (at (metro (+ 3.5 beat-number)) (weak-hat)))

; play a typical moombahton beat
(defn simple-moom [metro beat-number]

  ; FIXME: DRY
  (kicks metro beat-number)
  (snares metro beat-number)
  (hats metro beat-number)

  (apply-at (metro (+ 4 beat-number)) simple-moom metro (+ 4 beat-number) []))

; drums!
(defn drums []
  (simple-moom metro (metro)))

; required by leiningen (I believe), no real use here because
; arx is 100% repl for now
(defn -main [])

