# logchecker
Log parsing utility written in Java

### Example Usage
```
Usage: logchecker.jar [-v] -o=<outputFile> --logs=<logFiles>[,<logFiles>...]
                      [--logs=<logFiles>[,<logFiles>...]]... --rules=<ruleFiles>
                      [,<ruleFiles>...] [--rules=<ruleFiles>[,
                      <ruleFiles>...]]...
Parse the log files using the specified rules.
      --logs=<logFiles>[,<logFiles>...]
                  Log files to analyse
      --rules=<ruleFiles>[,<ruleFiles>...]
                  Rule files to analyse logs
  -o, --output=<outputFile>
                  Output report file
  -v, --verbose   Verbose progress output
```
```
java -jar logchecker.jar --verbose --logs=/Users/c0g/Logs/* --rules=/Users/c0g/Desktop/logchecker/rules/* --output=output.txt
```

### Example CLi Output
```
Analysing log [clarknet_access_log_Aug28] with Rule [XSS Composite]               100% [=================================================] 36/36 (03m, 07s)
done.

Took 03m, 07s to analyse 1052MB of logs 6 times.
================= RESULTS =================
Found a total of 13037 matches across 3 files.
File [/Users/c0g/Logs/clarknet_access_log_Aug28]: 9 matches
	 The rule XSS Composite matched 6 times
	 The rule Directory Traversal matched 3 times
File [/Users/c0g/Logs/stanmore_access.log]: 6 matches
	 The rule Very Active Client matched 6 times
File [/Users/c0g/Logs/almhuette_access.log]: 13022 matches
	 The rule XSS Composite matched 990 times
	 The rule Directory Traversal matched 2776 times
	 The rule SQL Injection Composite matched 1655 times
	 The rule Very Active Client matched 133 times
	 The rule Vulnerability scanner matched 7466 times
	 The rule Command Injection Composite matched 2 times
Output text report to file /Users/c0g/Desktop/logchecker/target/output.txt
```

### Example Report Output
```
XSS Composite matched  in file /Users/c0g/Logs/almhuette_access.log on line: 1553235
	91.218.225.68 - - [20/Jun/2018:09:19:47 +0200] "GET /sgdynamo.exe?HTNAME=<script>alert('Vulnerable')</script> HTTP/1.1" 404 218 "-" "Mozilla/5.00 (Nikto/2.1.6) (Evasions:None) (Test:003083)" "-"
XSS Composite matched  in file /Users/c0g/Logs/almhuette_access.log on line: 1551271
	91.218.225.68 - - [20/Jun/2018:09:17:36 +0200] "GET /administrator/gallery/navigation.php?directory=\\\"<script>alert(document.cookie)</script> HTTP/1.1" 404 242 "-" "Mozilla/5.00 (Nikto/2.1.6) (Evasions:None) (Test:000711)" "-"
```