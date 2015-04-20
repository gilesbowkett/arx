# arx

A Clojure rewrite (using Overtone) of a Ruby library called [Archaeopteryx](https://github.com/gilesbowkett/archaeopteryx). With arx, you can play (and gently mutate) drum and bass rhythms from your REPL.

# caveats

The code could be a lot more idiomatic; pull requests welcome, and my apologies in advance if [reading this makes your eyes bleed](http://xkcd.com/1513/).

Likewise, the drum samples were done in a hurry, and the drum rhythm's a truncated, tiny version of the [better rhythm](https://github.com/gilesbowkett/archaeopteryx/blob/midi_files/db_drum_definition.rb#L6) in the Archaeopteryx demo. 

## usage

Start here:

    lein repl

Then:

```clojure
  (drums)
  (random-beat) ; will produce a new beat every time you call it
  (main-loop)
  (variation)
  (stop)
```

## License

Copyright © 2014 Giles Bowkett

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.

