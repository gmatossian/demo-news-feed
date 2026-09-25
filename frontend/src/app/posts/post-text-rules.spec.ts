import { bodyProblem, normalizeText, textLength, titleProblem } from './post-text-rules';

// Same cases as backend/src/test/java/demo/newsfeed/post/PostTextTest.java.
describe('post text rules', () => {
  it('trims Unicode whitespace at the edges only', () => {
    expect(normalizeText(' \t Hello  world　\n')).toBe('Hello  world');
  });

  it('preserves internal formatting and normalizes line endings', () => {
    expect(normalizeText('\r\nFirst\r\n\r\n  - indented\rLast  \n')).toBe(
      'First\n\n  - indented\nLast',
    );
  });

  it('treats missing and whitespace-only text as empty', () => {
    expect(normalizeText(null)).toBe('');
    expect(titleProblem(' \n\t ')).toBe('Enter a title.');
    expect(bodyProblem('\r\n \r\n')).toBe('Enter the post text.');
  });

  it('counts code points so emoji count as one character', () => {
    expect(textLength('héllo 😀')).toBe(7);
    expect(titleProblem('😀'.repeat(120))).toBeNull();
    expect(titleProblem('😀'.repeat(121))).toBe('Title must be 120 characters or fewer.');
    expect(bodyProblem('a'.repeat(5000))).toBeNull();
    expect(bodyProblem('a'.repeat(5001))).toBe('Post text must be 5,000 characters or fewer.');
  });

  it('rejects multi-line titles', () => {
    expect(titleProblem('One\nTwo')).toBe('Title must be a single line.');
  });
});
