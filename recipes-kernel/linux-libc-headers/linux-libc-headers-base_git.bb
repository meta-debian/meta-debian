# based on OE-Core
require recipes-kernel/linux-libc-headers/linux-libc-headers.inc

# The current version of linux-cip is 4.4 which does not provide 'asm/bpf_perf_event.h'
# Overwrite do_install_armmultilib to remove 'asm/bpf_perf_event.h'.
# Fix error:
#   | ERROR: linux-libc-headers-base-gitAUTOINC+94e27e1f56-r0 do_install: oe_multilib_header: Unable to find header asm/bpf_perf_event.h.
do_install_armmultilib () {
	oe_multilib_header asm/auxvec.h asm/bitsperlong.h asm/byteorder.h asm/fcntl.h asm/hwcap.h asm/ioctls.h asm/kvm.h asm/kvm_para.h asm/mman.h asm/param.h asm/perf_regs.h
	oe_multilib_header asm/posix_types.h asm/ptrace.h  asm/setup.h  asm/sigcontext.h asm/siginfo.h asm/signal.h asm/stat.h  asm/statfs.h asm/swab.h  asm/types.h asm/unistd.h
}

# use the same kernel source as linux_git.bb
inherit linux-src

PROVIDES += "linux-libc-headers"
