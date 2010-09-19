/*
 * @(#) at_uni_salzburg_cs_ckgroup_io_PlainBluetoothSocketImpl.c
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

#include <at_uni_salzburg_cs_ckgroup_io_PlainBluetoothSocketImpl.h>

#include <string.h>
#include <termio.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/socket.h>
#include <sys/un.h>

#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>

#undef DEBUG
#ifdef DEBUG
#define debug(args...) fprintf (stderr, args)
#else
#define debug(args...)
#endif

/*
 * Class:     at_uni_salzburg_cs_ckgroup_io_PlainBluetoothSocketImpl
 * Method:    bluetoothSocketConnect
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_at_uni_1salzburg_cs_ckgroup_io_PlainBluetoothSocketImpl_bluetoothSocketConnect
  (JNIEnv * e, jobject self)
{
	jint fd = -2;
	char* bdaddr = "unknown";
	jint channel = -1;

	jclass selfClass = (*e)->GetObjectClass (e, self);

	jfieldID fdFieldId = (*e)->GetFieldID(e, selfClass, "fd", "Ljava/io/FileDescriptor;");
	jobject fdObject = (*e)->GetObjectField(e, self, fdFieldId);
	jclass fdClass = (*e)->GetObjectClass (e, fdObject);
	jfieldID fd_fdFieldId = (*e)->GetFieldID (e, fdClass, "fd", "I");
	fd = (*e)->GetIntField (e, fdObject, fd_fdFieldId);

	/* bdaddr field */
	jfieldID bdaddrFieldId = (*e)->GetFieldID (e, selfClass, "bdaddr", "Ljava/lang/String;");
	jobject bdaddrObject = (*e)->GetObjectField (e, self, bdaddrFieldId);
	const jbyte *bdaddrBuffer = (*e)->GetStringUTFChars (e, (jstring) bdaddrObject, NULL);
	char bdaddrBuf[100];
	strncpy (bdaddrBuf, (const char*) bdaddrBuffer, 100);
	bdaddr = bdaddrBuf;
	(*e)->ReleaseStringUTFChars (e, (jstring) bdaddrObject, bdaddrBuffer);

	/* channel field */
	jfieldID channelFieldId = (*e)->GetFieldID (e, selfClass, "channel", "I");
	channel = (*e)->GetIntField (e, self, channelFieldId);

	debug ("bluetoothSocketConnect fd=%d, bdaddr=%s, channel=%d\n", fd, bdaddr, channel);

	struct sockaddr_rc addr;
	jint s;
	int err;

	s = socket (PF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);
	addr.rc_family= AF_BLUETOOTH;
	bacpy (&addr.rc_bdaddr, BDADDR_ANY);
	addr.rc_channel = 0;
	err = bind (s, (struct sockaddr*)&addr, sizeof(addr));
	
	if (err < 0) {
		jclass ioExceptionClass = (*e)->FindClass(e, "Ljava/io/IOException;");
		(*e)->ThrowNew (e, ioExceptionClass, "Can not bind a bluetooth socket.");
		return;
	}

	debug ("bluetoothSocketConnect fd=%d, bdaddr=%s, channel=%d, bind ok\n", s, bdaddr, channel);
	addr.rc_family= AF_BLUETOOTH;
	str2ba (bdaddr, &addr.rc_bdaddr);
	addr.rc_channel = channel;
	err = connect (s, (struct sockaddr*)&addr, sizeof(addr));
	
	if (err < 0) {
		jclass ioExceptionClass = (*e)->FindClass(e, "Ljava/io/IOException;");
		(*e)->ThrowNew (e, ioExceptionClass, "Can not connect to bluetooth peer.");
		return;
	}

	debug ("bluetoothSocketConnect fd=%d, bdaddr=%s, channel=%d, connect ok\n", s, bdaddr, channel);

	(*e)->SetIntField (e, fdObject, fd_fdFieldId, s);
}

/*
 * Class:     at_uni_salzburg_cs_ckgroup_io_PlainBluetoothSocketImpl
 * Method:    bluetoothSocketClose
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_at_uni_1salzburg_cs_ckgroup_io_PlainBluetoothSocketImpl_bluetoothSocketClose
  (JNIEnv * e, jobject self)
{
	jint fd = -2;

	jclass selfClass = (*e)->GetObjectClass (e, self);

	jfieldID fdFieldId = (*e)->GetFieldID(e, selfClass, "fd", "Ljava/io/FileDescriptor;");
	jobject fdObject = (*e)->GetObjectField(e, self, fdFieldId);
	jclass fdClass = (*e)->GetObjectClass (e, fdObject);
	jfieldID fd_fdFieldId = (*e)->GetFieldID (e, fdClass, "fd", "I");
	fd = (*e)->GetIntField (e, fdObject, fd_fdFieldId);

	shutdown(fd, SHUT_RDWR);
	close (fd);

	debug ("bluetoothSocketClose fd=%d\n", fd);
}
