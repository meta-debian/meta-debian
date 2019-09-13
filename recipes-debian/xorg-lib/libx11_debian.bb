require libx11.inc
inherit gettext

BBCLASSEXTEND = "native nativesdk"

SRC_URI += "file://disable_tests.patch \
            file://0001-Fix-hanging-issue-in-_XReply.patch \
           "
# NOTE: Update Fix-hanging-issue-in-_XReply.patch for debian package.

do_configure_append () {
    sed -i -e "/X11_CFLAGS/d" ${B}/src/util/Makefile
}
