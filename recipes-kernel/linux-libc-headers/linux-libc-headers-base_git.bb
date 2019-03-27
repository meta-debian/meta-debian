# based on OE-Core
require recipes-kernel/linux-libc-headers/linux-libc-headers.inc

# use the same kernel source as linux_git.bb
inherit linux-src

PROVIDES += "linux-libc-headers"
