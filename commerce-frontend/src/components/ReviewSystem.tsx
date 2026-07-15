import { useState, useEffect } from 'react';
import { 
  Star, ThumbsUp, ThumbsDown, Image as ImageIcon, 
  Video, MessageSquare, Filter, SortAsc, CheckCircle,
  Send, X, ChevronDown, ChevronUp, Sparkles
} from 'lucide-react';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { Card } from './ui/card';
import { ScrollArea } from './ui/scroll-area';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './ui/tabs';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from './ui/dialog';
import { Textarea } from './ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Progress } from './ui/progress';

interface ReviewVideo {
  id: number;
  videoUrl: string;
  thumbnailUrl: string;
  uploadedAt: string;
}

interface ReviewReply {
  id: number;
  userId: number;
  userName: string;
  content: string;
  createdAt: string;
  updatedAt: string;
}

interface ReviewDetail {
  id: number;
  userId: number;
  userName: string;
  productId: number;
  productName: string;
  rating: number;
  comment: string;
  createdAt: string;
  verifiedPurchase: boolean;
  images: string[];
  videos: ReviewVideo[];
  helpfulCount: number;
  notHelpfulCount: number;
  userVotedHelpful: boolean;
  userVotedNotHelpful: boolean;
  replies: ReviewReply[];
}

interface RatingDistribution {
  fiveStar: number;
  fourStar: number;
  threeStar: number;
  twoStar: number;
  oneStar: number;
  averageRating: number;
  totalReviews: number;
}

interface ReviewSummary {
  productId: number;
  summary: string;
  pros: string;
  cons: string;
  generatedAt: string;
  updatedAt: string;
}

interface ReviewSystemProps {
  productId: number;
}

export function ReviewSystem({ productId }: ReviewSystemProps) {
  const [reviews, setReviews] = useState<ReviewDetail[]>([]);
  const [distribution, setDistribution] = useState<RatingDistribution | null>(null);
  const [summary, setSummary] = useState<ReviewSummary | null>(null);
  const [sortBy, setSortBy] = useState('recent');
  const [filter, setFilter] = useState('all');
  const [isLoading, setIsLoading] = useState(false);
  const [replyDialogOpen, setReplyDialogOpen] = useState(false);
  const [selectedReview, setSelectedReview] = useState<ReviewDetail | null>(null);
  const [replyContent, setReplyContent] = useState('');
  const [expandedReviews, setExpandedReviews] = useState<Set<number>>(new Set());

  useEffect(() => {
    fetchReviews();
    fetchDistribution();
    fetchSummary();
  }, [productId, sortBy, filter]);

  const fetchReviews = async () => {
    setIsLoading(true);
    try {
      const response = await fetch(
        `/api/reviews/enhanced/product/${productId}?sortBy=${sortBy}&filter=${filter}`,
        {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
        }
      );
      if (response.ok) {
        const data = await response.json();
        setReviews(data);
      }
    } catch (error) {
      console.error('Failed to fetch reviews:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const fetchDistribution = async () => {
    try {
      const response = await fetch(`/api/reviews/enhanced/product/${productId}/distribution`);
      if (response.ok) {
        const data = await response.json();
        setDistribution(data);
      }
    } catch (error) {
      console.error('Failed to fetch distribution:', error);
    }
  };

  const fetchSummary = async () => {
    try {
      const response = await fetch(`/api/reviews/enhanced/product/${productId}/summary`);
      if (response.ok) {
        const data = await response.json();
        setSummary(data);
      }
    } catch (error) {
      console.error('Failed to fetch summary:', error);
    }
  };

  const generateSummary = async () => {
    try {
      const response = await fetch(`/api/reviews/enhanced/product/${productId}/summary`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      });
      if (response.ok) {
        const data = await response.json();
        setSummary(data);
      }
    } catch (error) {
      console.error('Failed to generate summary:', error);
    }
  };

  const voteReview = async (reviewId: number, isHelpful: boolean) => {
    try {
      const response = await fetch(`/api/reviews/enhanced/${reviewId}/vote?isHelpful=${isHelpful}`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
      });
      if (response.ok) {
        fetchReviews();
      }
    } catch (error) {
      console.error('Failed to vote:', error);
    }
  };

  const submitReply = async () => {
    if (!selectedReview || !replyContent.trim()) return;
    try {
      const response = await fetch(`/api/reviews/enhanced/${selectedReview.id}/replies`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}` },
        body: JSON.stringify({ content: replyContent }),
      });
      if (response.ok) {
        fetchReviews();
        setReplyDialogOpen(false);
        setReplyContent('');
        setSelectedReview(null);
      }
    } catch (error) {
      console.error('Failed to submit reply:', error);
    }
  };

  const toggleExpand = (reviewId: number) => {
    setExpandedReviews(prev => {
      const newSet = new Set(prev);
      if (newSet.has(reviewId)) {
        newSet.delete(reviewId);
      } else {
        newSet.add(reviewId);
      }
      return newSet;
    });
  };

  const StarRating = ({ rating, size = 16 }: { rating: number; size?: number }) => (
    <div className="flex gap-1">
      {[1, 2, 3, 4, 5].map((star) => (
        <Star
          key={star}
          size={size}
          className={star <= rating ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'}
        />
      ))}
    </div>
  );

  const RatingBar = ({ count, total, color }: { count: number; total: number; color: string }) => {
    const percentage = total > 0 ? (count / total) * 100 : 0;
    return (
      <div className="flex items-center gap-2">
        <span className="text-sm w-8">{count}</span>
        <Progress value={percentage} className={`flex-1 ${color}`} />
        <span className="text-sm w-12 text-right">{percentage.toFixed(0)}%</span>
      </div>
    );
  };

  return (
    <div className="space-y-6">
      <Tabs defaultValue="reviews" className="w-full">
        <TabsList className="grid w-full grid-cols-2">
          <TabsTrigger value="reviews">Reviews</TabsTrigger>
          <TabsTrigger value="summary">AI Summary</TabsTrigger>
        </TabsList>

        <TabsContent value="reviews" className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Card className="p-6">
              <h3 className="font-semibold mb-4">Rating Distribution</h3>
              {distribution && (
                <div className="space-y-3">
                  <div className="flex items-center gap-2">
                    <span className="text-sm w-8">5</span>
                    <RatingBar count={distribution.fiveStar} total={distribution.totalReviews} color="bg-yellow-400" />
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-sm w-8">4</span>
                    <RatingBar count={distribution.fourStar} total={distribution.totalReviews} color="bg-yellow-400" />
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-sm w-8">3</span>
                    <RatingBar count={distribution.threeStar} total={distribution.totalReviews} color="bg-yellow-400" />
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-sm w-8">2</span>
                    <RatingBar count={distribution.twoStar} total={distribution.totalReviews} color="bg-yellow-400" />
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-sm w-8">1</span>
                    <RatingBar count={distribution.oneStar} total={distribution.totalReviews} color="bg-yellow-400" />
                  </div>
                  <Separator />
                  <div className="text-center">
                    <div className="text-3xl font-bold">{distribution.averageRating}</div>
                    <div className="text-sm text-muted-foreground">
                      out of 5 ({distribution.totalReviews} reviews)
                    </div>
                  </div>
                </div>
              )}
            </Card>

            <Card className="p-6 md:col-span-2">
              <div className="flex items-center justify-between mb-4">
                <h3 className="font-semibold">Reviews ({reviews.length})</h3>
                <div className="flex gap-2">
                  <Select value={sortBy} onValueChange={setSortBy}>
                    <SelectTrigger className="w-[140px]">
                      <SortAsc className="w-4 h-4 mr-2" />
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="recent">Recent</SelectItem>
                      <SelectItem value="helpful">Most Helpful</SelectItem>
                      <SelectItem value="highest">Highest Rated</SelectItem>
                      <SelectItem value="lowest">Lowest Rated</SelectItem>
                    </SelectContent>
                  </Select>
                  <Select value={filter} onValueChange={setFilter}>
                    <SelectTrigger className="w-[140px]">
                      <Filter className="w-4 h-4 mr-2" />
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="all">All Reviews</SelectItem>
                      <SelectItem value="verified">Verified Only</SelectItem>
                      <SelectItem value="with-images">With Images</SelectItem>
                      <SelectItem value="with-videos">With Videos</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <ScrollArea className="h-[500px]">
                {isLoading ? (
                  <div className="flex items-center justify-center h-64 text-muted-foreground">
                    Loading reviews...
                  </div>
                ) : reviews.length === 0 ? (
                  <div className="flex items-center justify-center h-64 text-muted-foreground">
                    No reviews yet
                  </div>
                ) : (
                  <div className="space-y-4">
                    {reviews.map((review) => (
                      <Card key={review.id} className="p-4">
                        <div className="flex items-start justify-between mb-3">
                          <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
                              {review.userName.charAt(0).toUpperCase()}
                            </div>
                            <div>
                              <div className="font-medium flex items-center gap-2">
                                {review.userName}
                                {review.verifiedPurchase && (
                                  <Badge variant="secondary" className="text-xs">
                                    <CheckCircle className="w-3 h-3 mr-1" />
                                    Verified
                                  </Badge>
                                )}
                              </div>
                              <div className="text-sm text-muted-foreground">
                                {new Date(review.createdAt).toLocaleDateString()}
                              </div>
                            </div>
                          </div>
                          <StarRating rating={review.rating} />
                        </div>

                        <p className="text-sm mb-3">{review.comment}</p>

                        {review.images.length > 0 && (
                          <div className="flex gap-2 mb-3">
                            {review.images.map((image, index) => (
                              <Dialog key={index}>
                                <DialogTrigger asChild>
                                  <div className="relative w-20 h-20 cursor-pointer rounded-lg overflow-hidden">
                                    <img src={image} alt={`Review image ${index + 1}`} className="w-full h-full object-cover" />
                                    <div className="absolute inset-0 bg-black/50 flex items-center justify-center opacity-0 hover:opacity-100 transition-opacity">
                                      <ImageIcon className="w-5 h-5 text-white" />
                                    </div>
                                  </div>
                                </DialogTrigger>
                                <DialogContent>
                                  <img src={image} alt={`Review image ${index + 1}`} className="w-full" />
                                </DialogContent>
                              </Dialog>
                            ))}
                          </div>
                        )}

                        {review.videos.length > 0 && (
                          <div className="flex gap-2 mb-3">
                            {review.videos.map((video) => (
                              <div key={video.id} className="relative w-32 h-20 cursor-pointer rounded-lg overflow-hidden">
                                <img src={video.thumbnailUrl} alt="Video thumbnail" className="w-full h-full object-cover" />
                                <div className="absolute inset-0 bg-black/50 flex items-center justify-center">
                                  <Video className="w-6 h-6 text-white" />
                                </div>
                              </div>
                            ))}
                          </div>
                        )}

                        <div className="flex items-center gap-4 mb-3">
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => voteReview(review.id, true)}
                            className={review.userVotedHelpful ? 'text-green-600' : ''}
                          >
                            <ThumbsUp className="w-4 h-4 mr-1" />
                            {review.helpfulCount}
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => voteReview(review.id, false)}
                            className={review.userVotedNotHelpful ? 'text-red-600' : ''}
                          >
                            <ThumbsDown className="w-4 h-4 mr-1" />
                            {review.notHelpfulCount}
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => {
                              setSelectedReview(review);
                              setReplyDialogOpen(true);
                            }}
                          >
                            <MessageSquare className="w-4 h-4 mr-1" />
                            Reply
                          </Button>
                        </div>

                        {review.replies.length > 0 && (
                          <div className="border-t pt-3">
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => toggleExpand(review.id)}
                              className="mb-2"
                            >
                              {expandedReviews.has(review.id) ? (
                                <ChevronUp className="w-4 h-4 mr-1" />
                              ) : (
                                <ChevronDown className="w-4 h-4 mr-1" />
                              )}
                              {review.replies.length} replies
                            </Button>
                            {expandedReviews.has(review.id) && (
                              <div className="space-y-2">
                                {review.replies.map((reply) => (
                                  <div key={reply.id} className="pl-4 border-l-2 border-muted">
                                    <div className="flex items-center gap-2 mb-1">
                                      <span className="font-medium text-sm">{reply.userName}</span>
                                      <span className="text-xs text-muted-foreground">
                                        {new Date(reply.createdAt).toLocaleDateString()}
                                      </span>
                                    </div>
                                    <p className="text-sm">{reply.content}</p>
                                  </div>
                                ))}
                              </div>
                            )}
                          </div>
                        )}
                      </Card>
                    ))}
                  </div>
                )}
              </ScrollArea>
            </Card>
          </div>
        </TabsContent>

        <TabsContent value="summary">
          <Card className="p-6">
            <div className="flex items-center justify-between mb-4">
              <h3 className="font-semibold flex items-center gap-2">
                <Sparkles className="w-5 h-5 text-yellow-500" />
                AI Review Summary
              </h3>
              {!summary && (
                <Button onClick={generateSummary}>
                  <Sparkles className="w-4 h-4 mr-2" />
                  Generate Summary
                </Button>
              )}
            </div>

            {summary ? (
              <div className="space-y-4">
                <div>
                  <h4 className="font-medium mb-2">Summary</h4>
                  <p className="text-muted-foreground">{summary.summary}</p>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="p-4 bg-green-50 dark:bg-green-900/20 rounded-lg">
                    <h4 className="font-medium mb-2 text-green-700 dark:text-green-400">Pros</h4>
                    <p className="text-sm text-muted-foreground">{summary.pros}</p>
                  </div>
                  <div className="p-4 bg-red-50 dark:bg-red-900/20 rounded-lg">
                    <h4 className="font-medium mb-2 text-red-700 dark:text-red-400">Cons</h4>
                    <p className="text-sm text-muted-foreground">{summary.cons}</p>
                  </div>
                </div>
                <div className="text-xs text-muted-foreground">
                  Last updated: {new Date(summary.updatedAt).toLocaleString()}
                </div>
              </div>
            ) : (
              <div className="text-center py-8 text-muted-foreground">
                <Sparkles className="w-12 h-12 mx-auto mb-4 opacity-50" />
                <p>Generate an AI summary of all reviews</p>
              </div>
            )}
          </Card>
        </TabsContent>
      </Tabs>

      <Dialog open={replyDialogOpen} onOpenChange={setReplyDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Reply to Review</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <Textarea
              placeholder="Write your reply..."
              value={replyContent}
              onChange={(e) => setReplyContent(e.target.value)}
              rows={4}
            />
            <div className="flex justify-end gap-2">
              <Button variant="outline" onClick={() => setReplyDialogOpen(false)}>
                Cancel
              </Button>
              <Button onClick={submitReply}>
                <Send className="w-4 h-4 mr-2" />
                Submit Reply
              </Button>
            </div>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
