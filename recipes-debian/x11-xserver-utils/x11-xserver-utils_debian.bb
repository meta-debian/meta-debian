# Because of conflicting with recipes from meta layer,
# x11-xserver-utils needs be split into multiple recipes.
#    | NOTE: multiple providers are available for runtime <package> (<package>, x11-xserver-utils)

require x11-xserver-utils.inc

DESCRIPTION = "An X client is a program that interfaces with an X server (almost always via\n\
the X libraries), and thus with some input and output hardware like a\n\
graphics card, monitor, keyboard, and pointing device (such as a mouse).\n\
.\n\
This package provides a miscellaneous assortment of X Server utilities\n\
that ship with the X Window System, including:\n\
 - iceauth, a tool for manipulating ICE protocol authorization records;\n\
 - rgb;\n\
 - sessreg, a simple program for managing utmp/wtmp entries;\n\
 - xcmsdb, a device color characteristic utility for the X Color Management\n\
   System;\n\
 - xgamma, a tool for querying and setting a monitor's gamma correction;\n\
 - xhost, a very dangerous program that you should never use;\n\
 - xmodmap, a utility for modifying keymaps and pointer button mappings in X;\n\
 - xrandr, a command-line interface to the RandR extension;\n\
 - xrdb, a tool to manage the X server resource database;\n\
 - xrefresh, a tool that forces a redraw of the X screen;\n\
 - xset, a tool for setting miscellaneous X server parameters;\n\
 - xsetmode and xsetpointer, tools for handling X Input devices;\n\
 - xsetroot, a tool for tailoring the appearance of the root window;\n\
 - xstdcmap, a utility to selectively define standard colormap properties;\n\
 - xvidtune, a tool for customizing X server modelines for your monitor."

LIC_FILES_CHKSUM = " \
    file://iceauth/COPYING;md5=13f70acf3c27f5f834bbc954df775f8e \
    file://rgb/COPYING;md5=ef598adbe241bd0b0b9113831f6e249a \
    file://sessreg/COPYING;md5=d938a70c8280c265a1ccd2954365d185 \
    file://xcmsdb/COPYING;md5=2f6f723cc96a47799cdc2427abd3ce28 \
    file://xgamma/COPYING;md5=ac9801b8423fd7a7699ccbd45cf134d8 \
    file://xhost/COPYING;md5=8fbed71dddf48541818cef8079124199 \
    file://xmodmap/COPYING;md5=272c17e96370e1e74773fa22d9989621 \
    file://xrandr/COPYING;md5=fe1608bdb33cf8c62a4438f7d34679b3 \
    file://xrdb/COPYING;md5=d1167c4f586bd41f0c62166db4384a69 \
    file://xrefresh/COPYING;md5=dad633bce9c3cd0e3abf72a16e0057cf \
    file://xset/COPYING;md5=bea81cc9827cdf1af0e12c2b8228cf8d \
    file://xsetmode/COPYING;md5=9b37e00e7793b667cbc64f9df7b6d733 \
    file://xsetpointer/COPYING;md5=9b37e00e7793b667cbc64f9df7b6d733 \
    file://xsetroot/COPYING;md5=6ea29dbee22324787c061f039e0529de \
    file://xstdcmap/COPYING;md5=2b08d9e2e718ac83e6fe2b974d4b5fd8 \
    file://xvidtune/COPYING;md5=fa0b9c462d8f2f13eba26492d42ea63d \
"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

ALLOW_EMPTY_${PN} = "1"
RDEPENDS_${PN} += "iceauth rgb sessreg xcmsdb xgamma xhost xmodmap \
                  xrandr xrdb xrefresh xset xsetmode xsetpointer \
                  xsetroot xstdcmap xvidtune \
                  "
