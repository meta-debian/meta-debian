# use simple kernel recipes instead of linux-yocto
VIRTUAL_KERNEL ??= "linux-base"
PREFERRED_PROVIDER_virtual/kernel = "${VIRTUAL_KERNEL}"
PREFERRED_PROVIDER_linux-libc-headers = "linux-libc-headers-base"
PREFERRED_PROVIDER_nativesdk-linux-libc-headers = "nativesdk-linux-libc-headers-base"
