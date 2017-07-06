SUMMARY = "lightweight C library for daemons"
DESCRIPTION = "libdaemon is a lightweight C library which eases the writing of UNIX daemons.\n\
 It consists of the following parts:\n\
 .\n\
  * Wrapper around fork() for correct daemonization of a process\n\
  * Wrapper around syslog() for simple log output to syslog or STDERR\n\
  * An API for writing PID files\n\
  * An API for serializing signals into a pipe for use with select() or poll()\n\
  * An API for running subprocesses with STDOUT and STDERR redirected to syslog\n\
 .\n\
 Routines like these are included in most of the daemon software available. It\n\
 is not simple to get these done right and code duplication is not acceptable."
HOMEPAGE = "http://0pointer.de/lennart/projects/libdaemon/"

inherit debian-package
PV = "0.14"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1"

inherit autotools
EXTRA_OECONF += "--disable-lynx"

RPROVIDES_${PN} += "libdaemon0"
