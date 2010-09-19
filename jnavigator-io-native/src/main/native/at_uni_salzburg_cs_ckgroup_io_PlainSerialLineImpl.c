/*
 * @(#) at_uni_salzburg_cs_ckgroup_io_PlainSerialLineImpl.c
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2009  Clemens Krainer
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

#include <at_uni_salzburg_cs_ckgroup_io_PlainSerialLineImpl.h>

#include <string.h>
#include <termio.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/socket.h>
#include <sys/un.h>

#define DEBUG
#ifdef DEBUG
#define debug(args...) fprintf (stderr, args)
#else
#define debug(args...)
#endif

/*
 * Class:     at_uni_salzburg_cs_ckgroup_io_PlainSerialLineImpl
 * Method:    serialLineOpen
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_at_uni_1salzburg_cs_ckgroup_io_PlainSerialLineImpl_serialLineOpen
  (JNIEnv * e, jobject self)
{
	jint fd = -2;
	char* name = "unknown";
	jint baud = -1;
	jint stopBits = -1;
	jint dataBits = -1;
	jint parity = -1;
	struct stat statBuffer;

	jclass selfClass = (*e)->GetObjectClass (e, self);

	jfieldID fdFieldId = (*e)->GetFieldID(e, selfClass, "fd", "Ljava/io/FileDescriptor;");
	jobject fdObject = (*e)->GetObjectField(e, self, fdFieldId);
	jclass fdClass = (*e)->GetObjectClass (e, fdObject);
	jfieldID fd_fdFieldId = (*e)->GetFieldID (e, fdClass, "fd", "I");
	fd = (*e)->GetIntField (e, fdObject, fd_fdFieldId);

	/* name field */
	jfieldID nameFieldId = (*e)->GetFieldID (e, selfClass, "name", "Ljava/lang/String;");
	jobject nameObject = (*e)->GetObjectField (e, self, nameFieldId);
	const jbyte *nameBuffer = (*e)->GetStringUTFChars (e, (jstring) nameObject, NULL);
	char nameBuf[100];
	strncpy (nameBuf, (const char*) nameBuffer, 100);
	name = nameBuf;
	(*e)->ReleaseStringUTFChars (e, (jstring) nameObject, nameBuffer);

	/* baud rate field */
	jfieldID baudRateFieldId = (*e)->GetFieldID (e, selfClass, "baudRate", "I");
	baud = (*e)->GetIntField (e, self, baudRateFieldId);
	
	/* data bits field */
	jfieldID dataBitsFieldId = (*e)->GetFieldID (e, selfClass, "dataBits", "I");
	dataBits = (*e)->GetIntField (e, self, dataBitsFieldId);
	
	/* stop bits field */
	jfieldID stopBitsFieldId = (*e)->GetFieldID (e, selfClass, "stopBits", "I");
	stopBits = (*e)->GetIntField (e, self, stopBitsFieldId);
	
	/* parity field */
	jfieldID parityFieldId = (*e)->GetFieldID (e, selfClass, "parity", "I");
	parity = (*e)->GetIntField (e, self, parityFieldId);

	
	if (stat (name, &statBuffer) < 0) {
		jclass ioExceptionClass = (*e)->FindClass(e, "Ljava/io/IOException;");
		(*e)->ThrowNew (e, ioExceptionClass, "Can not find specified path name.");
		return;
	}
	
	if (S_ISCHR (statBuffer.st_mode)) {
		fd = open (name, O_RDWR|O_NDELAY|O_NOCTTY|O_SYNC);

		debug ("serialLineOpen, fd=%d, name=%s, baud=0%o, databits=0%o, stopbits=0%o, parity=0%o\n",
			fd, name, baud, dataBits, stopBits, parity);
		
		if (fd < 0) {
			perror(name);
			jclass ioExceptionClass = (*e)->FindClass(e, "Ljava/io/IOException;");
			(*e)->ThrowNew (e, ioExceptionClass, "Can not open serial line.");
			return;
		}

	} else {
		
		if (S_ISSOCK (statBuffer.st_mode)) {
			struct sockaddr_un strAddr;
			socklen_t lenAddr;
	
			if ((fd=socket(PF_UNIX, SOCK_STREAM, 0)) < 0) {
				perror(name);
				jclass ioExceptionClass = (*e)->FindClass(e, "Ljava/io/IOException;");
				(*e)->ThrowNew (e, ioExceptionClass, "Can not open unix domain socket.");
				return;
			}
			
			strAddr.sun_family=AF_UNIX;
			strcpy(strAddr.sun_path, name);
			lenAddr=sizeof(strAddr.sun_family)+strlen(strAddr.sun_path);
			
			if (connect(fd, (struct sockaddr*)&strAddr, lenAddr) !=0 ) {
				perror(name);
				jclass ioExceptionClass = (*e)->FindClass(e, "Ljava/io/IOException;");
				(*e)->ThrowNew (e, ioExceptionClass, "Can not connect to unix domain socket.");
				return;				
			}
		}
	}
	

	(*e)->SetIntField (e, fdObject, fd_fdFieldId, fd);

	struct termios term;
        memset( &term, 0, sizeof( struct termios ) );

	term.c_cflag = (baud & CBAUDEX) | (dataBits & CSIZE) | (stopBits & CSTOPB) | CLOCAL | CREAD;
	term.c_iflag = parity;
	term.c_oflag = 0;
	term.c_lflag = 0;
	term.c_cc[VTIME]  = 0;
	term.c_cc[VMIN] = 1;    /* blocking read until 1 character arrives */

	fcntl(fd, F_SETFL, 0);	/* blocking read */
	tcflush (fd, TCIFLUSH);
	tcsetattr (fd, TCSANOW, &term);
}

/*
 * Class:     at_uni_salzburg_cs_ckgroup_io_PlainSerialLineImpl
 * Method:    serialLineClose
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_at_uni_1salzburg_cs_ckgroup_io_PlainSerialLineImpl_serialLineClose
  (JNIEnv * e, jobject self)
{
	jint fd = -2;

	jclass selfClass = (*e)->GetObjectClass (e, self);

	jfieldID fdFieldId = (*e)->GetFieldID(e, selfClass, "fd", "Ljava/io/FileDescriptor;");
	jobject fdObject = (*e)->GetObjectField(e, self, fdFieldId);
	jclass fdClass = (*e)->GetObjectClass (e, fdObject);
	jfieldID fd_fdFieldId = (*e)->GetFieldID (e, fdClass, "fd", "I");
	fd = (*e)->GetIntField (e, fdObject, fd_fdFieldId);

	close (fd);

	debug ("serialLineClose fd=%d\n", fd);
}
