(ns arx.core
  (:use overtone.live))

; drum sounds (sampled)
(def snare (sample (freesound-path 26903)))
(def kick (sample (freesound-path 2086)))
(def cowbell (sample (freesound-path 9780)))
(def tom (sample (freesound-path 184536)))
(def tom2 (sample (freesound-path 47700)))
(def zg-hat (sample (freesound-path 72526)))
(def click-hat (sample (freesound-path 183401)))

; drum sound (synthesized)
(definst hat [volume 1.0]
  (let [src (white-noise)
        env (env-gen (perc 0.001 0.1) :action FREE)]
    (* volume 0.7 src env)))

(def kick-beats (atom [0 1.5 3]))
(def snare-beats (atom [1 2.5]))
(def hat-beats (atom [0 0.5 1 1.5 2 2.5 3 3.5])) ; FIXME: DRY?
(def cowbell-beats (atom []))
(def tom-beats (atom []))
(def tom2-beats (atom []))
(def zg-hat-beats (atom []))
(def click-hat-beats (atom []))

(def kick-probabilities [1  0  0  0
                         0  0  1  0
                         0  0  0  0.1
                         1  0  0  0])

(def snare-probabilities  [
                            0 0 0 0
                            1 0 0 0
                            0 0 1 0
                            0 0 0 0
                          ])

(def hat-probabilities  [
                          0.55 0.15 0.55 0.15
                          0.55 0.15 0.55 0.15
                          0.55 0.15 0.55 0.15
                          0.55 0.15 0.55 0.15
                        ])

(def cowbell-probabilities [
                            0   0   0.2 0
                            0   0   0   0.1
                            0   0.1 0   0
                            0   0   0.2 0
                           ])

(def tom-probabilities [
                        0   0   0.2 0
                        0.4 0   0   0
                        0.2 0   0.3 0
                        0.3 0.1 0.3 0.4
                       ])

(def tom2-probabilities [
                         0.2 0   0.3 0
                         0.3 0.1 0.3 0.4
                         0   0   0.2 0
                         0.4 0   0   0
                        ])

(def zg-hat-probabilities [
                           0.65 0.65 0.65 0.65
                           0.65 0.65 0.65 0.65
                           0.65 0.65 0.65 0.65
                           0.65 0.65 0.65 0.65
                          ])

(def click-hat-probabilities [
                              0.9 0.9 0.9 0.9
                              0.9 0.9 0.9 0.9
                              0.9 0.9 0.9 0.9
                              0.9 0.9 0.9 0.9
                             ])

(def sieve-function (atom rand))

(defn random-drums [probabilities]
  (filter (fn [value]
              (not (nil? value)))
          (map-indexed (fn [idx prob]
                       (cond (< (@sieve-function) prob)
                                (* idx 0.25)))
                       probabilities)))

; FIXME: DRY
(defn random-kicks []
  (swap! kick-beats (fn [_] (random-drums kick-probabilities))))

(defn random-snares []
  (swap! snare-beats (fn [_] (random-drums snare-probabilities))))

(defn random-hats []
  (swap! hat-beats (fn [_] (random-drums hat-probabilities))))

(defn random-cowbells []
  (swap! cowbell-beats (fn [_] (random-drums cowbell-probabilities))))

(defn random-toms []
  (swap! tom-beats (fn [_] (random-drums tom-probabilities))))

(defn random-tom2s []
  (swap! tom2-beats (fn [_] (random-drums tom2-probabilities))))

(defn random-zg-hats []
  (swap! zg-hat-beats (fn [_] (random-drums zg-hat-probabilities))))

(defn random-click-hats []
  (swap! click-hat-beats (fn [_] (random-drums click-hat-probabilities))))

(defn random-beat []
  (random-kicks)
  (random-snares)
  (random-hats)
  (random-cowbells)
  (random-toms)
  (random-zg-hats)
  (random-click-hats)
  (random-tom2s)) ; FIXME DRY ZOMGWTF

(defn sieve [threshold]
  (swap! sieve-function (fn [_] (fn [] threshold)))
  (random-beat))

(defn random-sieve []
  (swap! sieve-function (fn [_] rand))
  (random-beat))

; the following three functions enable live-coding. to plug in new patterns,
; write code like this in the REPL:
;
;   (kicks [0 2 3.75]) ; for example
;
; in addition to being a nice idiom for coding live, this is good setup for
; the archaeopteryx-style generative breakbeats I have planned...

; FIXME: DRY
; might also be wiser to make these atoms too
(defn kicks [beats]
  (swap! kick-beats (fn [_] beats)))

(defn snares [beats]
  (swap! snare-beats (fn [_] beats)))

(defn hats [beats]
  (swap! hat-beats (fn [_] beats)))

; metronome
(def metro (atom (metronome 170)))

(defn generate-drum-series [drum beats beat-number]
  (doseq [beat beats]
    (at (@metro (+ beat beat-number)) (drum))))

; FIXME: DRY. the following three functions are nearly identical, and highly
; repetitious.
(defn generate-kicks [beat-number]
  (generate-drum-series kick @kick-beats beat-number))

(defn generate-snares [beat-number]
  (generate-drum-series snare @snare-beats beat-number))

(defn generate-hats [beat-number]
  (generate-drum-series hat @hat-beats beat-number))

(defn generate-cowbells [beat-number]
  (generate-drum-series cowbell @cowbell-beats beat-number))

(defn generate-toms [beat-number]
  (generate-drum-series tom @tom-beats beat-number)) ; FIXME DRY ZOMGWTF again!

(defn generate-tom2s [beat-number]
  (generate-drum-series tom2 @tom2-beats beat-number)) ; FIXME DRY ZOMGWTF again!

(defn generate-zg-hats [beat-number]
  (generate-drum-series zg-hat @zg-hat-beats beat-number)) ; FIXME DRY ZOMGWTF again!

(defn generate-click-hats [beat-number]
  (generate-drum-series click-hat @click-hat-beats beat-number)) ; FIXME DRY ZOMGWTF again!

(defn play-beat [beat-number]

    (doseq [generate-drums [generate-kicks
                            generate-snares
                            generate-hats
                            generate-cowbells
                            generate-toms
                            generate-zg-hats
                            generate-click-hats
                            generate-tom2s]] ; UGH DRY FIXME
      (generate-drums beat-number))

    (apply-at (@metro (+ 4 beat-number)) play-beat (+ 4 beat-number) []))

(defn drums []
  (play-beat (@metro)))

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

; use this to change tempo live. restarts loop >.<
(defn change-tempo [bpm]
  (swap! metro (fn [_] (metronome bpm)))
  (stop)
  (drums))

; required by leiningen (I believe), no real use here because
; arx is 100% repl for now
(defn -main [])

