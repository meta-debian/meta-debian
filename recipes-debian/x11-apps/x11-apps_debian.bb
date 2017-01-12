# Because of conflicting with recipes from meta layer,
# x11-apps needs be split into multiple recipes.
#    | NOTE: multiple providers are available for runtime <package> (<package>, x11-apps)

require x11-apps.inc

DESCRIPTION = "This package provides a miscellaneous assortment of X applications\n\
that ship with the X Window System, including:\n\
 - atobm, bitmap, and bmtoa, tools for manipulating bitmap images;\n\
 - ico, a demo program animating polyhedrons;\n\
 - oclock and xclock, graphical clocks;\n\
 - rendercheck, a program to test render extension implementations;\n\
 - transset, a tool to set opacity property on a window;\n\
 - xbiff, a tool which tells you when you have new email;\n\
 - xcalc, a scientific calculator desktop accessory;\n\
 - xclipboard, a tool to manage cut-and-pasted text selections;\n\
 - xconsole, which monitors system console messages;\n\
 - xcursorgen, a tool for creating X cursor files from PNGs;\n\
 - xditview, a viewer for ditroff output;\n\
 - xedit, a simple text editor for X;\n\
 - xeyes, a demo program in which a pair of eyes track the pointer;\n\
 - xgc, a graphics demo;\n\
 - xload, a monitor for the system load average;\n\
 - xlogo, a demo program that displays the X logo;\n\
 - xmag, which magnifies parts of the X screen;\n\
 - xman, a manual page browser;\n\
 - xmore, a text pager;\n\
 - xwd, a utility for taking window dumps ("screenshots") of the X session;\n\
 - xwud, a viewer for window dumps created by xwd;\n\
 - Xmark, x11perf, and x11perfcomp, tools for benchmarking graphical\n\
   operations under the X Window System;"

LIC_FILES_CHKSUM = " \
    file://bitmap/COPYING;md5=7966819d85ef9e09892d9161ce376270 \
    file://ico/COPYING;md5=83340fc8e3d8d0c4d23a4c62f6dd83a9 \
    file://oclock/COPYING;md5=43d419f7e7b7120f99d2d50739b7c5eb \
    file://rendercheck/COPYING;md5=0672108af794b575233e866a20cb267d \
    file://transset/COPYING;md5=2e56d5276d899b8afe6edd855ec6708b \
    file://x11perf/COPYING;md5=428ca4d67a41fcd4fc3283dce9bbda7e \
    file://xbiff/COPYING;md5=a4f198e2c1835cf956f36b5958ab99e7 \
    file://xcalc/COPYING;md5=1cc25ece7aa5c54ca7fd23b1c9111c23 \
    file://xclipboard/COPYING;md5=2b08d9e2e718ac83e6fe2b974d4b5fd8 \
    file://xclock/COPYING;md5=857759ade8f2ddde5c7b32ef7356ea36 \
    file://xconsole/COPYING;md5=3ae977a68975db2fb36e2e77081085bb \
    file://xcursorgen/COPYING;md5=bd1fb9ee90eead85f7b171807b3ab4f2 \
    file://xditview/COPYING;md5=e9902d8242e75ce1640bfd8bfaa4d561 \
    file://xedit/COPYING;md5=ee971579412993d428c07eef9d8ea585 \
    file://xeyes/COPYING;md5=3ea51b365051ac32d1813a7dbaa4bfc6 \
    file://xgc/COPYING;md5=78683538599762c70b92efd4b0a4cba7 \
    file://xload/COPYING;md5=95a73d76a420c774909b0df8cd45cbe3 \
    file://xlogo/COPYING;md5=c9365edf14143acf04c08b386ff7e2a4 \
    file://xmag/COPYING;md5=3413fe6832380b44b69b172d2d1b2387 \
    file://xman/COPYING;md5=20ffa1308d2bf5ee9bba5f06cf9cc17e \
    file://xmore/COPYING;md5=4641deddaa80fe7ca88e944e1fd94a94 \
    file://xwd/COPYING;md5=c0cdb783e9a0198237371fdaa26a18bf \
    file://xwud/COPYING;md5=31e8892c80b7a0c1c5f37c8e8ae6d794 \
"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} = "bitmap ico oclock rendercheck transset x11perf xbiff xcalc \
                  xclipboard xclock xconsole xcursorgen xditview xedit xeyes \
                  xgc xload xlogo xmag xman xmore xwd xwud \
                  "
