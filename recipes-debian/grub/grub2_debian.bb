#
# base recipe: meta/recipes-bsp/grub/grub_git.bb
# base branch: daisy
#

include grub2.inc

# configure.ac has code to set this automagically from the target tuple
# but the OE freeform one (core2-foo-bar-linux) don't work with that.

GRUBPLATFORM_arm = "uboot"
GRUBPLATFORM_aarch64 = "efi"
GRUBPLATFORM ??= "pc"

EXTRA_OECONF += "--with-platform=${GRUBPLATFORM}"
