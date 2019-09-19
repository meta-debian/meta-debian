# use simple kernel recipes instead of linux-yocto
PREFERRED_PROVIDER_virtual/kernel ?= "linux-base"
PREFERRED_PROVIDER_linux-libc-headers ?= "linux-libc-headers-base"
PREFERRED_PROVIDER_nativesdk-linux-libc-headers ?= "nativesdk-linux-libc-headers-base"
