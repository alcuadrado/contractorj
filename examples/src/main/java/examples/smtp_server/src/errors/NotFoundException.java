/**
 * **************************************************************************** $Workfile:
 * InvalidAddressException.java $ $Revision: 98 $ $Author: edaugherty $ $Date: 2003-10-01 14:30:25
 * -0500 (Wed, 01 Oct 2003) $
 *
 * <p>***************************************************************************** This program is
 * a 100% Java Email Server.
 * ***************************************************************************** Copyright (C) 2001,
 * Eric Daugherty All rights reserved.
 *
 * <p>This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>***************************************************************************** For current
 * versions and more information, please visit: http://www.ericdaugherty.com/java/mail
 *
 * <p>or contact the author at: java@ericdaugherty.com
 *
 * <p>***************************************************************************** This program is
 * based on the CSRMail project written by Calvin Smith. http://crsemail.sourceforge.net/
 * ***************************************************************************
 */
package examples.smtp_server.src.errors;

/**
 * Defines an exception when something required was not found.
 *
 * @author Eric Daugherty
 */
public class NotFoundException extends Exception {

  public NotFoundException() {
    super();
  }

  public NotFoundException(String message) {
    super(message);
  }
}
