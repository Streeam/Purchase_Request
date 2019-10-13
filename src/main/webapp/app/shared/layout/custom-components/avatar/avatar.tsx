import React from 'react';
import './avatar.css';
import { Link } from 'react-router-dom';

const avatar = props => {
  const { imageContentType, image, height, round, url, defaultSrc } = props;
  const avatarRadius = round ? '50%' : '100%';
  const classStyle = {
    maxHeight: height ? height : '100px',
    borderRadius: avatarRadius
  };
  return (
    <div className="container">
      {imageContentType && image ? (
        <img src={`data:${imageContentType};base64,${image}`} className="image" style={classStyle} />
      ) : (
        <img src={defaultSrc} className="image" style={classStyle} />
      )}
      {url ? (
        <Link to={url}>
          <div className="overlay">
            <p className="icon">Change</p>
          </div>
        </Link>
      ) : null}
    </div>
  );
};
export default avatar;
