var Login = function() {
	var handleAlerts = function() {
        $('body').on('click', '[data-close="alert"]', function(e) {
            $(this).parent('.alert').hide();
            $(this).closest('.note').hide();
            e.preventDefault();
        });

        $('body').on('click', '[data-close="note"]', function(e) {
            $(this).closest('.note').hide();
            e.preventDefault();
        });

        $('body').on('click', '[data-remove="note"]', function(e) {
            $(this).closest('.note').remove();
            e.preventDefault();
        });
    };
    return {
        //main function to initiate the module
        init: function() {
            // init background slide images
            $('.login-bg').backstretch([
                _baseContext +　"/lib/assets/pages/img/login/bg1.jpg",
                _baseContext +　"/lib/assets/pages/img/login/bg2.jpg",
                _baseContext +　"/lib/assets/pages/img/login/bg3.jpg"
                ], {
                  fade: 1000,
                  duration: 4000
                }
            );
            
            handleAlerts();
        }

    };

}();

jQuery(document).ready(function() {
    Login.init();
});