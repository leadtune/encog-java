rmdir /q /s .\source
rmdir /q /s .\dist
rmdir /q /s .\temp
md .\source
svn checkout http://encog-java.googlecode.com/svn/trunk/ .\source
cd source
cd encog-core
call ant clean
call ant
cd ..
cd encog-examples
call ant clean
call ant
cd ..
cd encog-workbench
call ant clean
call ant 
cd ..
cd ..
md .\temp
md .\dist
rem build the encog release files
md .\temp\encog-core
md .\temp\encog-core\examples
xcopy /s .\source\encog-core\* .\temp\encog-core
xcopy /s .\source\encog-examples\* .\temp\encog-core\examples
rmdir /q /s .\temp\encog-core\bin
rmdir /q /s .\temp\encog-core\examples\bin
cd temp
..\zip -r ..\dist\encog-core.zip .\encog-core
cd ..

rem build the work bench universial

md .\temp\workbench
md .\temp\workbench\jar
md .\temp\workbench\samples
copy .\source\encog-workbench\jar\*.* .\temp\workbench\jar
copy .\source\encog-workbench\lib\*.* .\temp\workbench\jar
copy .\source\encog-core\lib\*.* .\temp\workbench\jar
copy .\source\encog-workbench\samples\*.* .\temp\workbench\samples
copy .\source\encog-workbench\workbench.bat .\temp\workbench
copy .\source\encog-workbench\workbench.sh .\temp\workbench
copy .\source\encog-core\copyright.txt .\temp\workbench
cd temp
..\zip -r ..\dist\encog-workbench-universal.zip .\workbench
cd ..

rem build the work bench win32
copy .\source\encog-workbench\EncogWorkbench.exe .\temp\workbench\
cd temp
copy ..\*.nsh
copy ..\*.nsi
"c:\program files\nsis\makensis" EncogWorkbench.nsi
copy *.exe ..\dist
cd ..

rem build workbench source
rmdir /q /s .\source\encog-workbench\bin
cd source
..\zip -r ..\dist\encog-workbench-src.zip .\encog-workbench
cd ..