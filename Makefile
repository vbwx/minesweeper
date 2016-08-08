dist/build: $(wildcard src/minesweeper/*.java) $(wildcard src/minesweeper/*/*.java)
	[ -d .tmp ] || mkdir .tmp
	if javac -d .tmp $^; then               \
		[ -f $@ ] || echo 0 > $@;           \
		build=`awk '{ print $$1 + 1 }' $@`; \
		echo $$build > $@;                  \
	fi

dist/Minesweeper.jar: src/Manifest dist/build
	jar cfm $@ $< res -C .tmp minesweeper

.PHONY: all
all: dist/Minesweeper.jar

.PHONY: clean
clean:
	rm -rf .tmp
