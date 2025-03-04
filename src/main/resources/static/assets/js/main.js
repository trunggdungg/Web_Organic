console.log("Hello World!");

$('.owl-carousel').owlCarousel({
    loop:true,
    margin:20,
    nav:false,
    dots:true,
    autoplay:false,
    autoplayTimeout:3000,
    mouseDrag:false,
    responsive:{
        0:{
            items:1
        },
        600:{
            items:3
        },
        1000:{
            items:5
        }
    }
})


