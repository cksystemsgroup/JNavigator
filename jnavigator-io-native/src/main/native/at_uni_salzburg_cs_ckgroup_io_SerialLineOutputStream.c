/*
 * @(#) at_uni_salzburg_cs_ckgroup_io_SerialLineOutputStream.c
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

#include <at_uni_salzburg_cs_ckgroup_io_SerialLineOutputStream.h>

#undef DEBUG
#ifdef DEBUG
#define debug(args...) fprintf (stderr, args)
#else
#define debug(args...)
#endif

/*
 * Class:     at_uni_salzburg_cs_ckgroup_io_SerialLineOutputStream
 * Method:    serialLineWrite0
 * Signature: (Ljava/io/FileDescriptor;[BII)V
 */
JNIEXPORT void JNICALL Java_at_uni_1salzburg_cs_ckgroup_io_SerialLineOutputStream_serialLineWrite0
  (JNIEnv * e, jobject self, jobject fdObject, jbyteArray buf, jint ofs, jint len)
{
	jclass selfClass = (*e)->GetObjectClass (e, self);

	jclass fdClass = (*e)->GetObjectClass (e, fdObject);
	jfieldID fd_fdFieldId = (*e)->GetFieldID (e, fdClass, "fd", "I");
	int fd = (*e)->GetIntField (e, fdObject, fd_fdFieldId);

	jbyte    *data = (*e)->GetByteArrayElements(e, buf, NULL);

	int numBytes = write(fd, (char *)data + ofs, len);

#ifdef DEBUG
	debug ("serialLineWrite0 fd=%d, data=%p, ofs=%d, len=%d, numBytes=%d\n",
		fd, data, ofs, len, numBytes);

        int i;
        for (i=ofs; i < ofs+len; i++) {
                debug ("\tdata[%2d]=0x%02x, '%c'\n", i, data[i],
			data[i] > ' ' && data[i] < 127 ? data[i] : '.');
        }
#endif

	if (len != numBytes) {
		perror("write");
		jclass ioExceptionClass = (*e)->FindClass(e, "Ljava/io/IOException;");
		(*e)->ThrowNew (e, ioExceptionClass, "Can not write (enough) data to serial line.");
	}
}
