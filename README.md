# Huffman-Compression

(1)Place both henc.java and hdec.java into a folder

-Navigate to the folder containing both .java files;
	->i.e. If the .java files are placed inside your 'Documents';
		(command) 'cd /path/to/Documents'

(2)Compile both .java files
	(command) 'javac henc.java'
	(command) 'javac hdec.java'

(3)Place desired test files into the same folder as the .java files

-Copy desired file into a temporary file;
	(command) 'cp desiredFile tempFile'

*If the desired file is not in the same directory that you navigated to earlier, you MUST
	specify the complete path to the desired file;
		(command)'cp /path/to/desiredFile tempFile'*

(4)Encode the temporary file using the compiled henc program
	(command) 'java henc tempFile'

*tempFile should be automatically removed and replaced with a smaller sized file; 'tempFile.huf'*

(5)Decode the generated file using the compiled hdec program
	(command) 'java hdec tempFile.huf'

*tempFile.huf should be automatically removed and replaced with a file its original size; 'tempFile'*

(6)To check the validity of resulting file, compare it to the original file, 'desiredFile', in which 'tempFile' 
	was copied from earlier
		(command) 'diff desiredFile tempFile'
		(command) 'md5sum desiredFile tempFile'

*If file is in not in the same directory, specify the complete path to the file;
	(command) 'diff /path/to/desiredFile tempFile'
	(command) 'md5sum /path/to/desiredFile tempFile'*
