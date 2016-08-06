build: $(wildcard src/minesweeper/event/*.java) $(wildcard src/minesweeper/*.java) $(wildcard src/minesweeper/app/*.java)
	[ -d .tmp ] || mkdir .tmp
	javac -d .tmp $^

jar: src/Manifest
	jar cfm dist/Minesweeper.jar $< res -C .tmp minesweeper

.PHONY: clean
clean:
	rm -rf .tmp
