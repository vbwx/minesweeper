dist/build: $(wildcard src/minesweeper/event/*.java) $(wildcard src/minesweeper/*.java) $(wildcard src/minesweeper/app/*.java)
	[ -f $@ ] || echo 0 > $@
	build=`awk '{ print $$1 + 1 }' $@`; echo $$build > $@
	[ -d .tmp ] || mkdir .tmp
	javac -d .tmp $^

dist/Minesweeper.jar: src/Manifest dist/build
	jar cfm $@ $< res -C .tmp minesweeper

.PHONY: all
all: dist/Minesweeper.jar

.PHONY: clean
clean:
	rm -rf .tmp
