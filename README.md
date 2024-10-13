# hw-topics-git-project

1.  Did you code stage() / how well does it work?
My stage calls the addBlob() and addDir() methods already present within Michael's code, throwing proper exceptions where nessesary. I have tested it extensively and the method itself works perfectly. I haven't caught any major bugs in the addBlob() or addDir() methods yet, so I would assume they work as well.

2.  Did you code commit() / how well does it work?
commit() works very well, I even added a version where you don't add an author message and it'll automatically grab the current system user (the implementation with author name as an input still works). I tested it repeatedly for adding files and directories, and it correctly handles creating the linked list and the other stuff. No issues so far

3. Did you do checkout / how well does it work?
I did code checkout, and it seems to work perfectly. The implementation is pretty poor, where it wipes every change and then recreates it from the initial commit recursively, but this does make it so it always updates code to the most recent edit made. Overall no issues, but the implementation could be improved.

4. What bugs did find / which of em did you fix?
I encountered a few bugs in very slight things, such as an edgecase check that accidentally forgot to include /git at the start or a minor formatting thing. All known bugs have been fixed.