SUMMARY = "high-performance event loop library modelled after libevent"
DESCRIPTION = "libev provides a full-featured and high-performance event loop that is \
loosely modelled after libevent. It includes relative timers, absolute \
timers with customized rescheduling, synchronous signals, process status \
change events, event watchers dealing with the event loop itself, file \
watchers, and even limited support for fork events. It uses a priority \
queue to manage timers and uses arrays as fundamental data structure. It \
has no artificial limitations on the number of watchers waiting for the \
same event."
HOMEPAGE = "http://libev.schmorp.de/"

inherit debian-package
PV = "4.15"

LICENSE = "BSD-2-Clause | GPLv2+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a4460a29fc20be7a1a2e6c95660ec740"

inherit autotools

do_install_append() {
	# Remove event.h to avoid conflict with libevent
	rm -f ${D}${includedir}/event.h
}
