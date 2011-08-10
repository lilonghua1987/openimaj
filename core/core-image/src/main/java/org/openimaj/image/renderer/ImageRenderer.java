package org.openimaj.image.renderer;

import java.text.AttributedString;
import java.util.Arrays;
import java.util.List;

import org.openimaj.image.Image;
import org.openimaj.image.pixel.ConnectedComponent;
import org.openimaj.image.processor.connectedcomponent.render.BlobRenderer;
import org.openimaj.image.typography.Font;
import org.openimaj.image.typography.FontRenderer;
import org.openimaj.image.typography.FontStyle;
import org.openimaj.math.geometry.line.Line2d;
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.math.geometry.point.Point2dImpl;
import org.openimaj.math.geometry.shape.Polygon;
import org.openimaj.math.geometry.shape.Shape;

/**
 * ImageRenderer is the abstract bae class for all renderers
 * capable of drawing to images. 
 * 
 * @author Jonathon Hare <jsh2@ecs.soton.ac.uk>
 *
 * @param <Q> Pixel type
 * @param <I> Image type
 */
public abstract class ImageRenderer<Q, I extends Image<Q, I>> {
	protected RenderHints hints;
	protected I targetImage;
	
	/**
	 * Construct with given target image.
	 * @param targetImage the target image.
	 */
	public ImageRenderer(I targetImage) {
		this(targetImage, new RenderHints());
	}
	
	/**
	 * Construct with given target image and rendering hints.
	 * @param targetImage the target image.
	 * @param hints the render hints
	 */
	public ImageRenderer(I targetImage, RenderHints hints) {
		this.targetImage = targetImage;
		this.hints = hints;
	}
	
	/**
	 * Draw onto this image lines drawn with the given colour between the
	 * points given. No points are drawn. Side-affects this image.
	 *  
	 * @param pts The point list to draw onto this image.
	 * @param col The colour to draw the lines
	 */
	public void drawConnectedPoints(List<? extends Point2d> pts, Q col) {
		Point2d p0 = pts.get(0);  
		for (int i=1; i<pts.size(); i++) {
			Point2d p1 = pts.get(i);
			
			int x0 = Math.round(p0.getX());
			int y0 = Math.round(p0.getY());
			int x1 = Math.round(p1.getX());
			int y1 = Math.round(p1.getY());
			
			drawLine(x0, y0, x1, y1,col);
			
			p0 = p1;
		}
	}

	/**
	 * Draw into this image the provided image at the given coordinates.
	 * Parts of the image outside the bounds of this image
	 * will be ignored. Side-affects this image.
	 * 
	 * @param image The image to draw. 
	 * @param x The x-coordinate of the top-left of the image
	 * @param y The y-coordinate of the top-left of the image
	 */
	public void drawImage(I image, int x, int y) {
		int stopx = Math.min(targetImage.getWidth(), x + image.getWidth());
		int stopy = Math.min(targetImage.getHeight(), y + image.getHeight());
		int startx = Math.max(0, x);
		int starty = Math.max(0, y);
		
		for (int yy=starty; yy<stopy; yy++)
			for (int xx=startx; xx<stopx; xx++)
				targetImage.setPixel(xx, yy, image.getPixel(xx-x,yy-y));
	}
	
	/**
	 * Draw into this image the provided image at the given coordinates ignoring
	 * certain pixels. Parts of the image outside the bounds of this image will 
	 * be ignored. Side-affects this image. Pixels in the ignore list will be
	 * stripped from the image to draw.
	 * 
	 * @param image The image to draw. 
	 * @param x The x-coordinate of the top-left of the image
	 * @param y The y-coordinate of the top-left of the image
	 * @param ignoreList The list of pixels to ignore when copying the image
	 */
	public void drawImage(I image, int x, int y, Q ... ignoreList) {
		int stopx = Math.min(targetImage.getWidth(), x + image.getWidth());
		int stopy = Math.min(targetImage.getHeight(), y + image.getHeight());
		int startx = Math.max(0, x);
		int starty = Math.max(0, y);
		
		for (int yy=starty; yy<stopy; yy++)
			for (int xx=startx; xx<stopx; xx++)
			{
				Q val = image.getPixel(xx-x, yy-y);
				if(Arrays.binarySearch(ignoreList, val, targetImage.getPixelComparator())<0)
					targetImage.setPixel(xx, yy, val);
			}
				
	}

	/**
	 * Draw a line from the coordinates specified by <code>(x1,y1)</code> 
	 * at an angle of <code>theta</code> with the given length, thickness
	 * and colour. Side-affects this image.
	 * 
	 * @param x1 The x-coordinate to start the line.
	 * @param y1 The y-coordinate to start the line.
	 * @param theta The angle at which to draw the line.
	 * @param length The length to draw the line.
	 * @param thickness The thickness to draw the line.
	 * @param col The colour to draw the line.
	 */
	public abstract void drawLine(int x1, int y1, double theta, int length, int thickness, Q col);
	
	/**
	 * Draw a line from the coordinates specified by <code>(x1,y1)</code> 
	 * at an angle of <code>theta</code> with the given length and colour.
	 * Line-thickness will be 1. Side-affects this image.
	 * 
	 * @param x1 The x-coordinate to start the line.
	 * @param y1 The y-coordinate to start the line.
	 * @param theta The angle at which to draw the line.
	 * @param length The length to draw the line.
	 * @param col The colour to draw the line.
	 */
	public void drawLine(int x1, int y1, double theta, int length, Q col) {
		drawLine(x1, y1, theta, length, 1, col);
	}
	
	/**
	 * Draw a line from the coordinates specified by <code>(x0,y0)</code> to 
	 * the coordinates specified by <code>(x1,y1)</code> using the given 
	 * color and thickness. Side-affects this image.
	 * 
	 * @param x0 The x-coordinate at the start of the line.
	 * @param y0 The y-coordinate at the start of the line. 
	 * @param x1 The x-coordinate at the end of the line.
	 * @param y1 The y-coordinate at the end of the line.
	 * @param thickness The thickness which to draw the line.
	 * @param col The colour in which to draw the line.
	 */
	public abstract void drawLine(int x0, int y0, int x1, int y1, int thickness, Q col);

	/**
	 * Draw a line from the coordinates specified by <code>(x0,y0)</code> to 
	 * <code>(x1,y1)</code> using the given colour. The line thickness will
	 * be 1 pixel. Side-affects this image.
	 * 
	 * @param x0 The x-coordinate at the start of the line.
	 * @param y0 The y-coordinate at the start of the line. 
	 * @param x1 The x-coordinate at the end of the line.
	 * @param y1 The y-coordinate at the end of the line.
	 * @param col The colour in which to draw the line.
	 */
	public void drawLine(int x0, int y0, int x1, int y1, Q col) {
		drawLine(x0, y0, x1, y1, 1, col);
	}
	
	/**
	 * Draw a line from the specified Line2d object
	 * 
	 * @param line the line
	 * @param thickness the stroke width
	 * @param col The colour in which to draw the line.
	 */
	public void drawLine(Line2d line, int thickness, Q col) {
		drawLine((int)line.begin.getX(), (int)line.begin.getY(), (int)line.end.getX(), (int)line.end.getY(), thickness, col);
	}

	/**
	 * Draw a dot centered on the given location (rounded to nearest integer 
	 * location) at the given size and with the given color. 
	 * Side-affects this image. 
	 * 
	 * @param p The coordinates at which to draw the point 
	 * @param col The colour to draw the point
	 * @param size The size at which to draw the point.
	 */
	public abstract void drawPoint(Point2d p, Q col, int size);

	/**
	 * Draw the given list of points using {@link #drawPoint(Point2d, Object, int)}
	 * with the given colour and size. Side-affects this image.
	 * 
	 * @param pts The list of points to draw.
	 * @param col The colour to draw each point.
	 * @param size The size to draw each point.
	 */
	public void drawPoints(Iterable<? extends Point2d> pts, Q col, int size) {
		for (Point2d p : pts)
			drawPoint(p, col, size);
	}
	
	/**
	 * Draw the given polygon in the specified colour with the given thickness lines.
	 * Side-affects this image.
	 * 
	 * @param p The polygon to draw.
	 * @param thickness The thickness of the lines to use
	 * @param col The colour to draw the lines in
	 */
	public abstract void drawPolygon(Polygon p, int thickness, Q col);

	/**
	 * Draw the given polygon in the specified colour. Uses
	 * {@link #drawPolygon(Polygon, int, Object)} with line thickness 1.
	 * Side-affects this image.
	 * 
	 * @param p The polygon to draw.
	 * @param col The colour to draw the polygon in.
	 */
	public void drawPolygon(Polygon p, Q col) {
		drawPolygon(p, 1, col);
	}

	/**
	 * Draw the given polygon, filled with the specified colour.
	 * Side-affects this image.
	 * 
	 * @param p The polygon to draw.
	 * @param col The colour to fill the polygon with.
	 */
	public void drawPolygonFilled(Polygon p, Q col) {
		drawPolygon(p, col);
		
		ConnectedComponent cc = new ConnectedComponent(p);
		cc.process(new BlobRenderer<Q>(targetImage, col));
	}
	
	/**
	 * Draw the given shape in the specified colour with the given thickness lines.
	 * Side-affects this image.
	 * 
	 * @param s The shape to draw.
	 * @param thickness The thickness of the lines to use
	 * @param col The colour to draw the lines in
	 */
	public void drawShape(Shape s, int thickness, Q col) {
		drawPolygon(s.asPolygon(), thickness, col);
	}

	/**
	 * Draw the given shape in the specified colour. Uses
	 * {@link #drawPolygon(Polygon, int, Object)} with line thickness 1.
	 * Side-affects this image.
	 * 
	 * @param p The shape to draw.
	 * @param col The colour to draw the polygon in.
	 */
	public void drawShape(Shape p, Q col) {
		drawShape(p, 1, col);
	}

	/**
	 * Draw the given shape, filled with the specified colour. 
	 * Side-affects this image.
	 * 
	 * @param s The shape to draw.
	 * @param col The colour to fill the polygon with.
	 */
	public void drawShapeFilled(Shape s, Q col) {
		drawShape(s, col);
		
		ConnectedComponent cc = new ConnectedComponent(s);
		cc.process(new BlobRenderer<Q>(targetImage, col));
	}

	/**
	 * Render the text in the given font with the default style.
	 * 
	 * @param <F> the font
	 * @param text the text
	 * @param x the x-ordinate
	 * @param y the y-ordinate
	 * @param f the font
	 * @param sz the size
	 */
	public <F extends Font<F>> void drawText(String text, int x, int y, Font<F> f, int sz) {
		FontStyle<F, Q> sty = f.createStyle(this);
		sty.setFontSize(sz);
		f.getRenderer(this).renderText(this, text, x, y, sty);
	}
	
	/**
	 * Render the text in the given font in the given colour with the default style.
	 * 
	 * @param <F> the font
	 * @param text the text
	 * @param x the x-ordinate
	 * @param y the y-ordinate
	 * @param f the font
	 * @param sz the size
	 * @param col the font color
	 */
	public <F extends Font<F>> void drawText(String text, int x, int y, Font<F> f, int sz, Q col) {
		FontStyle<F, Q> sty = f.createStyle(this);
		sty.setFontSize(sz);
		sty.setColour(col);
		f.getRenderer(this).renderText(this, text, x, y, sty);
	}
	
	/**
	 * Render the text in the given font with the default style.
	 * 
	 * @param <F> the font
	 * @param text the text
	 * @param pt the coordinate to render at
	 * @param f the font
	 * @param sz the size
	 */
	public <F extends Font<F>> void drawText(String text, Point2d pt, Font<F> f, int sz) {
		FontStyle<F, Q> sty = f.createStyle(this);
		sty.setFontSize(sz);
		f.getRenderer(this).renderText(this, text, (int)pt.getX(), (int)pt.getY(), sty);
	}
	
	/**
	 * Render the text in the given font in the given colour with the default style.
	 * 
	 * @param <F> the font
	 * @param text the text
	 * @param pt the coordinate to render at
	 * @param f the font
	 * @param sz the size
	 * @param col the font colour
	 */
	public <F extends Font<F>> void drawText(String text, Point2d pt, Font<F> f, int sz, Q col) {
		FontStyle<F, Q> sty = f.createStyle(this);
		sty.setFontSize(sz);
		sty.setColour(col);
		f.getRenderer(this).renderText(this, text, (int)pt.getX(), (int)pt.getY(), sty);
	}
	
	/**
	 * Render the text with the given {@link FontStyle}.
	 * 
	 * @param <F> the font
	 * @param text the text
	 * @param x the x-ordinate
	 * @param y the y-ordinate
	 * @param f the font style
	 */
	public <F extends Font<F>> void drawText(String text, int x, int y, FontStyle<F,Q> f) {
		f.getRenderer(this).renderText(this, text, x, y, f);
	}
	
	/**
	 * Render the text with the given {@link FontStyle}.
	 * 
	 * @param <F> the font
	 * @param text the text
	 * @param pt the coordinate to render at
	 * @param f the font style
	 */
	public <F extends Font<F>> void drawText(String text, Point2d pt, FontStyle<F,Q> f) {
		f.getRenderer(this).renderText(this, text, (int)pt.getX(), (int)pt.getY(), f);
	}
	
	/**
	 * Render the text using its attributes.
	 * 
	 * @param text the text
	 * @param x the x-ordinate
	 * @param y the y-ordinate
	 */
	public void drawText(AttributedString text, int x, int y) {
		FontRenderer.renderText(this, text, x, y);
	}
	
	/**
	 * Render the text using its attributes.
	 * 
	 * @param text the text
	 * @param pt the coordinate to render at
	 */
	public void drawText(AttributedString text, Point2d pt) {
		FontRenderer.renderText(this, text, (int)pt.getX(), (int)pt.getY());
	}
	
	/**
	 * 	Calculates straight line segments along a Bezier curve.	
	 * 
	 *	@param nPoints The number of points to divide the bezier curve into
	 *	@param p1 The point at the end of the line
	 *	@param p2 The point at the other end of the line
	 *	@param c1 The control point associated with p1
	 *	@param c2 The control point associated with p2
	 *	@return An array of points representing points along the curve
	 */
	protected Point2d[] computeBezierPoints( int nPoints, Point2d p1, 
			Point2d p2, Point2d c1, Point2d c2 )
	{
		Point2d[] bezier = new Point2d[nPoints];
		
		float inc = (1f/nPoints);
		float t = 0;
		
		for( int j = 0; j < nPoints; j++ )
		{
			float t1 = 1 - t;
			
			float t13 = t1*t1*t1;
			float t13a = 3*t*t1*t1;
			float t13b = 3*t*t*t1;
			float t13c = t*t*t;
			
			float x = t13 * p1.getX();
			x += t13a * c1.getX();
			x += t13b * c2.getX();
			x += t13c * p2.getX();
			
			float y = t13 * p1.getY();
			y += t13a * c1.getY();
			y += t13b * c2.getY();
			y += t13c * p2.getY();
			
			bezier[j] = new Point2dImpl( x, y );
			
			t += inc;
		}
		
		return bezier;
	}

	/**
	 * 	Draw a cubic Bezier curve into the image with 100 point accuracy.
	 * 
	 *	@param p1 One end point of the line
	 *	@param p2 The other end point of the line
	 *	@param c1 The control point associated with p1
	 *	@param c2 The control point associated with p2
	 *	@param thickness The thickness to draw the line
	 *	@param col The colour to draw the line
	 *	@return The points along the bezier curve
	 */
	public Point2d[] drawCubicBezier( Point2d p1, Point2d p2, 
			Point2d c1, Point2d c2, int thickness, Q col )
	{
		return drawCubicBezier( p1, p2, c1, c2, thickness, col, 100 );
	}
	
	/**
	 * 	Draw a cubic Bezier curve into the image with the given accuracy
	 * 
	 *	@param p1 One end point of the line
	 *	@param p2 The other end point of the line
	 *	@param c1 The control point associated with p1
	 *	@param c2 The control point associated with p2
	 *	@param thickness The thickness to draw the line
	 *	@param col The colour to draw the line
	 *	@param nPoints The number of points to divide the curve into
	 *	@return The points along the bezier curve
	 */
	public Point2d[] drawCubicBezier( Point2d p1, Point2d p2, 
			Point2d c1, Point2d c2, int thickness, Q col, int nPoints )
	{
		Point2d[] b = computeBezierPoints( nPoints, p1, p2, c1, c2 );
		
		Point2d last = null;
		for( Point2d p : b )
		{
			if( last != null )
				drawLine( (int)last.getX(), (int)last.getY(), 
						  (int)p.getX(), (int)p.getY(), 3, col );
			last = p;
		}
		
		return b;
	}
	
	/**
	 * Get the default foreground colour.
	 * @return the default foreground colour.
	 */
	public abstract Q defaultForegroundColour();
	
	/**
	 * Get the default foreground colour.
	 * @return the default foreground colour.
	 */
	public abstract Q defaultBackgroundColour();
	
	/**
	 * Get the target image
	 * @return the image
	 */
	public I getImage() {
		return targetImage;
	}
	
	/**
	 * Change the target image of this renderer.
	 * @param image new target
	 */
	public void setImage(I image) {
		this.targetImage = image;
	}
	
	/**
	 * Get the render hints object associated
	 * with this renderer
	 * @return the render hints
	 */
	public RenderHints getRenderHints() {
		return this.hints;
	}
	
	/**
	 * Set the render hints associated with this
	 * renderer
	 * @param hints the new hints
	 */
	public void setRenderHints(RenderHints hints) {
		this.hints = hints;
	}
}