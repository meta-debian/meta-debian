SUMMARY = "pango library X backend"
DESCRIPTION = "Pango is a library for layout and rendering of text, with an emphasis\n\
on internationalization. Pango can be used anywhere that text layout is\n\
needed. however, most of the work on Pango-1.0 was done using the GTK+\n\
widget toolkit as a test platform. Pango forms the core of text and\n\
font handling for GTK+-2.0.\n\
.\n\
Pango is designed to be modular; the core Pango layout can be used with\n\
four different font backends:\n\
 - Core X windowing system fonts\n\
 - Client-side fonts on X using the Xft library\n\
 - Direct rendering of scalable fonts using the FreeType library\n\
 - Native fonts on Microsoft backends"

inherit debian-package
PV = "0.0.2"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"

DEPENDS = "pango glib-2.0 virtual/libx11"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit autotools pkgconfig distro_features_check

# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

RPROVIDES_${PN} += "libpangox-1.0"
RPROVIDES_${PN}-dev += "libpangox-1.0-dev"
