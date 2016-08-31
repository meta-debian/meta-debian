SUMMARY = "Plugins for the GStreamer multimedia framework"
DESCRIPTION = "\
GStreamer is a streaming media framework, based on graphs of filters \
which operate on media data.  Applications using this library can do \
anything from real-time sound processing to playing videos, and just \
about anything else media-related.  Its plugin-based architecture means \
that new data types or processing capabilities can be added simply by \
installing new plug-ins. \
. \
This package contains the GStreamer plugins from the 'base' set, an \
essential exemplary set of elements."
HOMEPAGE = "http://gstreamer.freedesktop.org/"

DEPENDS = "gstreamer"

inherit autotools pkgconfig

GSTREAMER_DEBUG ?= "--disable-debug"
EXTRA_OECONF = "--disable-valgrind ${GSTREAMER_DEBUG} --disable-examples "

acpaths = "-I ${S}/common/m4 -I ${S}/m4"

PACKAGES_DYNAMIC += "^${PN}-.*"

# apply gstreamer hack after Makefile.in.in in source is replaced by our version from
# ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in, but before configure is executed 
# http://lists.linuxtogo.org/pipermail/openembedded-core/2012-November/032233.html
oe_runconf_prepend() {
	if [ -e ${S}/po/Makefile.in.in ]; then
		sed -i -e "1a\\" -e 'GETTEXT_PACKAGE = @GETTEXT_PACKAGE@' ${S}/po/Makefile.in.in
	fi
}
PARALLEL_MAKE = ""
