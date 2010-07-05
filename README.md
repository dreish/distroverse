# distroverse

Distroverse is a project intended to develop a distributed virtual
reality system, with a roughly web-like protocol and a browser that
uses it.

## Installation

The instructions below take some liberties assuming you are on a Unixy
system (I use OS X mostly) at a shell prompt, have a ~/bin directory
in your path, and have a ~/src directory where you put source code,
whether your own or other people's.

Make sure you have java installed.  1.6 is good:

    $ java -version
    java version "1.6.0_20"
    Java(TM) SE Runtime Environment (build 1.6.0_20-b02-279-10M3065)
    Java HotSpot(TM) 64-Bit Server VM (build 16.3-b01-279, mixed mode)

Install Leiningen if you don't already have it.  Java (in the unlikely
event you don't already have it), Leiningen, and Distroverse itself
are the only things you need to download.  You don't need to install
Clojure yourself or any libraries; Leiningen takes care of it all for
you:

    $ wget -nv http://github.com/technomancy/leiningen/raw/stable/bin/lein
    2010-07-05 16:32:34 URL:http://..../lein [4061/4061] -> "lein" [1]
    $ mv lein ~/bin
    $ lein self-install
    Downloading Leiningen now...
      % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                     Dload  Upload   Total   Spent    Left  Speed
    100 8076k  100 8076k    0     0   963k      0  0:00:08  0:00:08 --:--:-- 1040k


Get Distroverse:

    $ cd ~/src
    $ git clone git://github.com/dreish/distroverse.git
    $ cd distroverse

Build it:

    $ lein deps
    [various download messages omitted]
    $ lein uberjar

Run it:

    $ java -jar distroverse-standalone.jar

Hack away!

## Usage

The only usage right now is to add code until distroverse actually
does something.

## License

Copyright (C) 2007-2010 Dan Reish

Distroverse is distributed under the Eclipse Public License, just like
Clojure.  You can find various GPL'd and LGPL'd files in the earlier
incarnation of the project in git, and that code may still, of coures,
be distributed under those licenses.  If you need me to relicense some
of the older code under the EPL, let me know.
