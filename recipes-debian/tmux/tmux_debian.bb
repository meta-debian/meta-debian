SUMMARY = "terminal multiplexer"
DESCRIPTION = "tmux enables a number of terminals (or windows) to be accessed and \
controlled from a single terminal like screen. tmux runs as a \
server-client system. A server is created automatically when necessary \
and holds a number of sessions, each of which may have a number of \
windows linked to it. Any number of clients may connect to a session, \
or the server may be controlled by issuing commands with tmux. \
Communication takes place through a socket, by default placed in /tmp. \
Moreover tmux provides a consistent and well-documented command \
interface, with the same syntax whether used interactively, as a key \
binding, or from the shell. It offers a choice of vim or Emacs key layouts."
HOMEPAGE = "http://tmux.sourceforge.net/"

PR = "r0"

inherit debian-package
PV = "1.9"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://COPYING;md5=f7d9aab84ec6567139a4755c48d147fb"

DEPENDS = "ncurses libevent"

# Remove hardcode host path in Makefile.am to avoid QA Issue
SRC_URI += "file://Makefile_am-remove-host-path.patch"

inherit autotools-brokensep pkgconfig
