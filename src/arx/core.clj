(ns arx.core
  (:use overtone.live))

; drum sounds (sampled)
(def snare (sample (freesound-path 26903)))
(def kick (sample (freesound-path 2086)))

; drum sound (synthesized)
(definst hat [volume 1.0]
  (let [src (white-noise)
        env (env-gen (perc 0.001 0.1) :action FREE)]
    (* volume 0.7 src env)))

(def tempo (atom 170))

(def kick-beats (atom [0 1.5 3]))
(def snare-beats (atom [1 2.5]))
(def hat-beats (atom [0 0.5 1 1.5 2 2.5 3 3.5])) ; FIXME: DRY?

; the following three functions enable live-coding. to plug in new patterns,
; write code like this in the REPL:
;
;   (kicks [0 2 3.75]) ; for example
;
; in addition to being a nice idiom for coding live, this is good setup for
; the archaeopteryx-style generative breakbeats I have planned...

(defn kicks [beats]
  (swap! kick-beats (fn [_] beats)))

(defn snares [beats]
  (swap! snare-beats (fn [_] beats)))

(defn hats [beats]
  (swap! hat-beats (fn [_] beats)))

; metronome
(def metro (metronome @tempo))
; FIXME: changing tempos is not as simple as updating @tempo. it should be.
; I blame this function, and several others which follow.

; FIXME: DRY. way too many repetitions of "metro beat-number" all up
; in this motherfucker.
(defn generate-drum-series [drum beats metro beat-number]
  (doseq [beat beats]
    (at (metro (+ beat beat-number)) (drum))))

; FIXME: DRY. the following three functions are nearly identical, and highly
; repetitious.
(defn generate-kicks [metro beat-number]
  (generate-drum-series kick @kick-beats metro beat-number))

(defn generate-snares [metro beat-number]
  (generate-drum-series snare @snare-beats metro beat-number))

(defn generate-hats [metro beat-number]
  (generate-drum-series hat @hat-beats metro beat-number))

; FIXME: the way these following two functions divide their logic around
; setting up a metronome seems pretty fucking stupid. but it may be
; necessary to keep the same metronome in play from function call to
; function call. the metronome isn't really a metronome at all, it's a
; kind of ongoing tempo holder. maybe it should be an atom?
(defn play-beat [metro beat-number]

  (doseq [generate-drums [generate-kicks generate-snares generate-hats]]
    (generate-drums metro beat-number))

  (apply-at (metro (+ 4 beat-number)) play-beat metro (+ 4 beat-number) []))

(defn drums []
  (play-beat metro (metro)))

; use these to do paint-by-numbers live-coding; just fire off (variation)
; or (main-loop) to switch from one to the other in the REPL
(defn main-loop []
  (kicks [0 1.5 3])
  (snares [1 2.5])
  (hats [0 0.5 1 1.5 2 2.5 3 3.5])) ; FIXME: DRY?

(defn variation []
  (kicks [0 2 2.5])
  (snares [1 3])
  (hats [0 0.25 0.5 0.75 1 1.25 1.5 1.75 2 2.25 2.5 2.75 3 3.25 3.5 3.75])) ; FIXME: DRY?

; required by leiningen (I believe), no real use here because
; arx is 100% repl for now
(defn -main [])

