dist/build: $(wildcard src/minesweeper/*.java) $(wildcard src/minesweeper/*/*.java)
	@[ -d .tmp ] || mkdir .tmp
	@if javac -Xlint:unchecked -d .tmp $^; then \
		[ -f $@ ] || echo 0 > $@; \
		build=`awk '{ print $$1 + 1 }' $@`; \
		echo $$build > $@; \
	fi

dist/Minesweeper.jar: src/Manifest dist/build
	jar cfm $@ $< res -C .tmp minesweeper

.PHONY: all clean
all: dist/Minesweeper.jar

clean:
	rm -rf .tmp
