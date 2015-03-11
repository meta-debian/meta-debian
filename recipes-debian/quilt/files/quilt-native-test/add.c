#include <stdio.h>
#include <stdlib.h>

int main(int argc, char **argv)
{
	if (argc < 3)
		return argc;
	printf("Hello, I'm Test\n");
	printf("%d\n", atoi(argv[1]) + atoi(argv[2]));
	return 0;
}
