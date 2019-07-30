import React, { Component } from 'react';

export default ({ children }) => <span dangerouslySetInnerHTML={{ __html: children }} />;
