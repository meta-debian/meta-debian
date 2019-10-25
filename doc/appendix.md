Appendix
========

Version comparison
------------------
Below is the packages version comparison between Debian 10 and Poky.
The comparison is done with Poky 2.6 (thud), 2.7 (warrior) and 2.8 (master).

*Note: Poky 2.8 has not released yet, so we use the current branch master instead.*
```
meta-debian : 51c6c0a175b8e3a91bec803a0a792fed00029e6e
poky 2.6    : 50f33d3bfebcbfb1538d932fb487cfd789872026
poky 2.7    : 0e392026ffefee098a890c39bc3ca1f697bacb52
poky 2.8    : c23c8ebc7f66a92110bfc9e3c4d633a432d1353b
```

*Note: Packages with version NA mean they are not provided.*

|            | Debian 10       | Poky 2.6        | Poky 2.7        | Poky 2.8        |
|------------|-----------------|-----------------|-----------------|-----------------|
| autoconf   | 2.69            | 2.69            | 2.69            | 2.69            |
| automake   | 1.16.1          | 1.16.1          | 1.16.1          | 1.16.1          |
| bc         | 1.07.1          | 1.07.1          | 1.07.1          | 1.07.1          |
| binutils   | 2.31.1          | 2.31.1          | **2.32.0**      | **2.32.0**      |
| bison      | 3.3.2           | **3.0.4**       | **3.0.4**       | **3.0.4**       |
| busybox    | 1.30.1          | **1.29.3**      | 1.30.1          | 1.30.1          |
| bzip2      | 1.0.6           | 1.0.6           | 1.0.6           | 1.0.6           |
| curl       | 7.64.0          | **7.61.0**      | 7.64.0          | **7.64.1**      |
| db         | 5.3.28          | 5.3.28          | 5.3.28          | 5.3.28          |
| dbus       | 1.12.12         | **1.12.10**     | 1.12.12         | 1.12.12         |
| ed         | 1.15            | **1.14.2**      | 1.15            | 1.15            |
| expat      | 2.2.6           | 2.2.6           | 2.2.6           | 2.2.6           |
| flex       | 2.6.4           | **2.6.0**       | **2.6.0**       | **2.6.0**       |
| gawk       | 4.2.1           | 4.2.1           | 4.2.1           | 4.2.1           |
| gcc        | 8.3.0           | **8.2.0**       | 8.3.0           | 8.3.0           |
| gdb        | 8.2.1           | **8.2**         | 8.2.1           | 8.2.1           |
| gettext    | 0.19.8.1        | 0.19.8.1        | 0.19.8.1        | 0.19.8.1        |
| git        | 2.20.1          | **2.18.1**      | 2.20.1          | 2.20.1          |
| glib-2.0   | 2.58.3          | **2.58.0**      | 2.58.3          | 2.58.3          |
| glibc      | 2.28            | 2.28            | **2.29**        | **2.29**        |
| gmp        | 6.1.2           | 6.1.2           | 6.1.2           | 6.1.2           |
| gnulib     | 20140202+stable | **NA**          | **NA**          | **NA**          |
| gzip       | 1.9             | 1.9             | **1.10**        | **1.10**        |
| kbd        | 2.0.4           | 2.0.4           | 2.0.4           | 2.0.4           |
| libtool    | 2.4.6           | 2.4.6           | 2.4.6           | 2.4.6           |
| libxml2    | 2.9.4           | **2.9.8**       | **2.9.8**       | **2.9.8**       |
| m4         | 1.4.18          | 1.4.18          | 1.4.18          | 1.4.18          |
| make       | 4.2.1           | 4.2.1           | 4.2.1           | 4.2.1           |
| libmpc     | 1.1.0           | 1.1.0           | 1.1.0           | 1.1.0           |
| mpfr       | 4.0.2           | **4.0.1**       | 4.0.2           | 4.0.2           |
| ncurses    | 6.1+20181013    | **6.1+20180630**| 6.1+20181013    | 6.1+20181013    |
| openssl    | 1.1.1b          | **1.1.1a**      | 1.1.1b          | 1.1.1b          |
| pax-utils  | 1.2.4           | **NA**          | **NA**          | **NA**          |
| pciutils   | 3.5.2           | **3.6.2**       | **3.6.2**       | **3.6.2**       |
| perl       | 5.28.1          | **5.24.4**      | 5.28.1          | 5.28.1          |
| pkgconfig  | 0.29            | **0.29.2**      | **0.29.2**      | **0.29.2**      |
| popt       | 1.16            | 1.16            | 1.16            | 1.16            |
| procps     | 3.3.15          | 3.3.15          | 3.3.15          | 3.3.15          |
| quilt      | 0.65            | 0.65            | 0.65            | 0.65            |
| rdma-core  | 22.1            | **NA**          | **NA**          | **NA**          |
| readline   | 7.0             | 7.0             | **8.0**         | **8.0**         |
| sysfsutils | 2.1.0           | 2.1.0           | 2.1.0           | 2.1.0           |
| tar        | 1.30            | 1.30            | **1.31**        | **1.32**        |
| u-boot     | 2019.01         | **2018.07**     | 2019.01         | **2019.04**     |
| unifdef    | 2.10            | **2.11**        | **2.11**        | **2.11**        |
| zlib       | 1.2.11          | 1.2.11          | 1.2.11          | 1.2.11          |


|                                                    | Poky 2.6 | Poky 2.7 | Poky 2.8 |
|----------------------------------------------------|----------|----------|----------|
| Number of packages with same version               | 24       | 32       | 30       |
| Number of packages with lower version than Debian  | 15       | 2        | 2        |
| Number of packages with higher version than Debian | 4        | 9        | 11       |
| Number of packages are not provided in Poky        | 3        | 3        | 3        |

Poky 2.8 is planned to be released in Oct 2019, so the number of packages
with different version may continue increase in future.